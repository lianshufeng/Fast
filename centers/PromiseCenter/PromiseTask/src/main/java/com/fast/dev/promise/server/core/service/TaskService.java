package com.fast.dev.promise.server.core.service;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.net.apache.HttpModel;
import com.fast.dev.core.util.net.apache.MethodType;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.promise.model.ErrorTryModel;
import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.model.ResponseStatusModel;
import com.fast.dev.promise.model.TaskModel;
import com.fast.dev.promise.server.core.config.PromiseTaskConfig;
import com.fast.dev.promise.server.core.dao.ErrorTryTableDao;
import com.fast.dev.promise.server.core.dao.HttpTableDao;
import com.fast.dev.promise.server.core.dao.TaskTableDao;
import com.fast.dev.promise.server.core.domain.ErrorTryTable;
import com.fast.dev.promise.server.core.domain.HttpTable;
import com.fast.dev.promise.server.core.domain.TaskTable;
import com.fast.dev.promise.server.core.helper.TaskHelper;
import com.fast.dev.promise.type.TaskState;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Log
@Service
public class TaskService {


    @Autowired
    private TaskTableDao taskTableDao;

    @Autowired
    private HttpTableDao httpTableDao;

    @Autowired
    private ErrorTryTableDao errorTryTableDao;

    @Autowired
    private TaskHelper taskHelper;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private PromiseTaskConfig promiseTaskConfig;


    /**
     * 添加任务
     *
     * @return
     */
    @Transactional
    public RequestParmModel put(RequestParmModel parm) {
        if (parm.getId() == null) {
            parm.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        }


        TaskModel taskModel = new TaskModel();
        taskModel.setHeartbeatTime(this.dbHelper.getTime());
        BeanUtils.copyProperties(parm, taskModel);


        TaskTable taskTable = this.taskTableDao.findFirstByTaskId(taskModel.getId());
        ErrorTryTable errorTryTable = null;
        HttpTable httpTable = null;
        if (taskTable == null) {
            taskTable = new TaskTable();
            this.taskTableDao.save(taskTable);
            errorTryTable = new ErrorTryTable();
            httpTable = new HttpTable();
        } else {
            errorTryTable = taskTable.getErrorTryTable();
            httpTable = taskTable.getHttpTable();
        }

        //TaskTable
        BeanUtils.copyProperties(taskModel, taskTable, "id");
        taskTable.setTaskId(taskModel.getId());
        taskTable.setHeartbeatTime(taskModel.getHeartbeatTime());
        taskTable.setTaskState(TaskState.Wait);


        //ErrorTryTable
        BeanUtils.copyProperties(taskModel.getErrorTry(), errorTryTable);
        errorTryTable.setTaskTable(taskTable);
        this.errorTryTableDao.save(errorTryTable);

        //HttpTable
        BeanUtils.copyProperties(taskModel.getHttp(), httpTable, "header", "body");
        if (taskModel.getHttp().getBody() != null) {
            httpTable.setBody(JsonUtil.toJson(taskModel.getHttp().getBody()));
        }
        if (taskModel.getHttp().getHeader() != null) {
            httpTable.setHeader(JsonUtil.toJson(taskModel.getHttp().getHeader()));
        }
        httpTable.setTaskTable(taskTable);
        this.httpTableDao.save(httpTable);

        //TaskTable
        taskTable.setErrorTryTable(errorTryTable);
        taskTable.setHttpTable(httpTable);
        this.taskTableDao.save(taskTable);

        return taskModel;
    }


    /**
     * 删除任务
     *
     * @return
     */
    @Transactional
    public Map<String, Boolean> removeTaskTableByTaskId(String... ids) {
        Map<String, Boolean> m = new HashMap<>();
        for (String id : ids) {
            m.put(id, this.taskTableDao.removeTaskTableByTaskId(id));
        }
        return m;
    }


    /**
     * 执行任务
     *
     * @return
     */
    @Transactional
    public boolean doit(String id) {
        TaskTable taskTable = this.taskTableDao.canDoTask(id);
        if (taskTable == null) {
            return false;
        }

        addTask(new ArrayList<TaskTable>() {{
            add(taskTable);
        }});

        return true;
    }


    /**
     * 获取任务
     *
     * @param id
     * @return
     */

    public TaskModel query(String id) {
        TaskTable taskTable = this.taskTableDao.findFirstByTaskId(id);
        return taskTable2Model(taskTable);
    }


    /**
     * 数据实体转model
     *
     * @return
     */
    public TaskModel taskTable2Model(TaskTable taskTable) {
        if (taskTable == null) {
            return null;
        }

        TaskModel taskModel = new TaskModel();
        BeanUtils.copyProperties(taskTable, taskModel);
        taskModel.setId(taskTable.getTaskId());

        //http
        HttpModel httpModel = new HttpModel();
        HttpTable httpTable = taskTable.getHttpTable();
        BeanUtils.copyProperties(httpTable, httpModel, "header", "body");
        if (httpTable.getHeader() != null) {
            try {
                httpModel.setHeader(JsonUtil.toObject(httpTable.getHeader(), Map.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (httpTable.getBody() != null) {
            Class type = (httpTable.getMethod() == MethodType.Json ? Map.class : String.class);
            try {
                httpModel.setBody(JsonUtil.toObject(httpTable.getBody(), type));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        taskModel.setHttp(httpModel);

        //errorTry
        ErrorTryModel errorTryModel = new ErrorTryModel();
        ErrorTryTable errorTryTable = taskTable.getErrorTryTable();
        BeanUtils.copyProperties(errorTryTable, errorTryModel);
        taskModel.setErrorTry(errorTryModel);

        return taskModel;

    }


    /**
     * 设置心跳任务
     */
    public Map<String, Boolean> heartbeat(String... ids) {
        if (ids != null && ids.length > 0) {
            return this.taskTableDao.setHeartbeatTime(ids);
        }
        return new HashMap<>();
    }


    /**
     * 设置尝试执行任务
     *
     * @param taskId
     */
    public void setTryDoWorkById(String taskId) {
        this.taskTableDao.setTryDoWorkById(taskId);
    }


    /**
     * 执行错误的任务
     */
    @Transactional
    public void doErrorTask() {
        addTask(this.taskTableDao.findCanDoErrorTask(getCanLoadMaxTaskCount()));
    }


    /**
     * 执行任务
     */
    @Transactional
    public void doTask() {
        addTask(this.taskTableDao.findCanDoTask(getCanLoadMaxTaskCount()));
    }

    /**
     * 获取可以载入的任务数量
     *
     * @return
     */
    private int getCanLoadMaxTaskCount() {
        int maxCount = this.promiseTaskConfig.getMaxExecuteTaskCount() - this.taskHelper.getCurrentTaskCount();
        if (maxCount < 0) {
            maxCount = 0;
        }
        return maxCount;
    }

    /**
     * 添加任务
     *
     * @param taskTables
     */
    private void addTask(List<TaskTable> taskTables) {
        if (taskTables != null && taskTables.size() > 0) {
            log.info("获取任务数： " + taskTables.size());
            for (TaskTable taskTable : taskTables) {
                this.taskHelper.doit(taskTable);
            }
        }
    }


    /**
     * 获取下次执行时间
     *
     * @param taskModel
     * @return
     */
    private long getNextTime(TaskModel taskModel) {
        return taskModel.getExecuteTime() - (this.dbHelper.getTime() - taskModel.getHeartbeatTime());
    }


    /**
     * 获取响应的模型
     *
     * @param id
     * @return
     */
    public ResponseStatusModel getResponseModel(String id) {
        TaskModel taskModel = this.query(id);
        if (taskModel == null) {
            return null;
        }
        return ResponseStatusModel.builder().nextExecuteTime(getNextTime(taskModel)).build();
    }


}
