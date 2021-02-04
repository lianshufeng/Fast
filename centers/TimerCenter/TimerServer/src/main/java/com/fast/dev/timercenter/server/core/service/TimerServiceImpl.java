package com.fast.dev.timercenter.server.core.service;

import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.core.util.net.apache.HttpClientUtil;
import com.fast.dev.core.util.net.apache.HttpModel;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.timercenter.server.core.conf.DefaultRequestParmConf;
import com.fast.dev.timercenter.server.core.dao.TaskTableDao;
import com.fast.dev.timercenter.server.core.domain.CheckTable;
import com.fast.dev.timercenter.server.core.domain.TaskTable;
import com.fast.dev.timercenter.service.model.*;
import com.fast.dev.timercenter.service.service.RemoteManagerTimerService;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class TimerServiceImpl implements RemoteManagerTimerService {

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private TaskTableDao taskTableDao;


    @Autowired
    private DefaultRequestParmConf defaultRequestParmModelConf;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    @Transactional
    public ResponseTaskModel put(RequestParmModel parm) {
        if (!StringUtils.hasText(parm.getTaskId())) {
            parm.setTaskId(UUID.randomUUID().toString().replaceAll("-", ""));
        }

        //查询db，载入且保存
        TaskTable taskTable = findAndSaveTask(parm.getTaskId());

        //复制数据并同步到数据库里
        copyObject(parm, taskTable);
        copyObject(parm.getHttp(), taskTable.getHttpModel());
        copyObject(parm.getCheck(), taskTable.getCheckTable());

        this.dbHelper.updateTime(taskTable);
        this.mongoTemplate.save(taskTable);


        //转换为输出对象
        return toResponseModel(taskTable);
    }

    @Override
    public ResponseTaskModel query(String taskId) {
        TaskTable taskTable = this.taskTableDao.findFirstByTaskId(taskId);
        if (taskTable == null) {
            return null;
        }
        return toResponseModel(taskTable);
    }

    @Override
    public Map<String, Boolean> heartbeat(String... id) {
        return this.taskTableDao.heartbeatTime(id);
    }

    @Override
    public Map<String, Boolean> remove(String... taskId) {
        return this.taskTableDao.removeTaskTableByTaskId(taskId);
    }


    @Override
    @SneakyThrows
    public Object doit(String id) {
        final Object[] result = {null};
        this.taskTableDao.removeByTaskId(id).ifPresent((taskTable) -> {
            try {
                result[0] = HttpClientUtil.request(taskTable.getHttpModel());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return result[0];
    }


    /**
     * 任务模型转换为响应的结果模型
     *
     * @param taskTable
     * @return
     */
    private ResponseTaskModel toResponseModel(TaskTable taskTable) {
        ResponseTaskModel responseTaskModel = new ResponseTaskModel();
        //先把实体转换为任务模型
        TaskModel taskModel = toTaskModel(taskTable);
        BeanUtils.copyProperties(taskModel, responseTaskModel);

        //计算下次执行的时间
        long executeTime = taskModel.getDelayTime()  - (this.dbHelper.getTime() - taskModel.getHeartbeatTime());
        responseTaskModel.setStatus(new ResponseStatusModel());
        responseTaskModel.getStatus().setNextExecuteTime(executeTime > 0 ? executeTime : 0);

        return responseTaskModel;
    }

    /**
     * 实体转换为模型
     *
     * @param taskTable
     * @return
     */
    public TaskModel toTaskModel(TaskTable taskTable) {

        TaskModel taskModel = new TaskModel();
        taskModel.setHttp(new HttpModel());
        taskModel.setCheck(new CheckModel());
        BeanUtils.copyProperties(taskTable, taskModel);


        HttpModel httpModel = taskTable.getHttpModel();
        if (httpModel != null) {
            BeanUtils.copyProperties(httpModel, taskModel.getHttp());
        }


        CheckTable checkTable = taskTable.getCheckTable();
        if (checkTable != null) {
            BeanUtils.copyProperties(checkTable, taskModel.getCheck());
        }


        return taskModel;
    }


    /**
     * 查找并保存任务
     *
     * @param taskId
     * @return
     */
    private TaskTable findAndSaveTask(String taskId) {
        //存在就直接读取数据库
        if (StringUtils.hasText(taskId)) {
            TaskTable taskTable = this.taskTableDao.findFirstByTaskId(taskId);
            if (taskTable != null) {
                return taskTable;
            }
        }

        //不存在则新建
        TaskTable taskTable = new TaskTable();
        BeanUtils.copyProperties(defaultRequestParmModelConf, taskTable, "taskId");
        taskTable.setTaskId(taskId);
        taskTable.setHeartbeatTime(this.dbHelper.getTime());


        //http请求模型
        HttpModel httpModel = new HttpModel();
        BeanUtils.copyProperties(defaultRequestParmModelConf.getHttp(), httpModel);
        taskTable.setHttpModel(httpModel);

        //补偿
        CheckTable checkTable = new CheckTable();
        BeanUtils.copyProperties(defaultRequestParmModelConf.getCheck(), checkTable);
        taskTable.setCheckTable(checkTable);

        this.dbHelper.saveTime(taskTable);
        this.mongoTemplate.save(taskTable);

        return taskTable;
    }


    /**
     * 拷贝对象
     *
     * @param source
     * @param target
     */
    private void copyObject(Object source, Object target) {
        Set<String> names = new HashSet<>();
        BeanUtil.getNullPropertyNames(source, names);
        BeanUtils.copyProperties(source, target, names.toArray(new String[0]));
    }

}
