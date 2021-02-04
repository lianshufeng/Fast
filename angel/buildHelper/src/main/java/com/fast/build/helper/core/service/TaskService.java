package com.fast.build.helper.core.service;

import com.fast.build.helper.core.conf.BuildTaskConf;
import com.fast.build.helper.core.helper.ProjectHelper;
import com.fast.build.helper.core.model.ProjectItem;
import com.fast.build.helper.core.model.ProjectTask;
import com.fast.build.helper.core.model.TaskProgress;
import com.fast.build.helper.core.task.ProjectTaskRunnable;
import com.fast.build.helper.core.type.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 任务
 */
@Service
public class TaskService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ProjectHelper projectHelper;

    @Autowired
    private BuildTaskConf buildTaskConf;

    //任务列表
    private Vector<TaskProgress> tasks = new Vector();

    //任务线程池
    private ExecutorService taskManagerThreadPool;

    @PostConstruct
    private void init() {
        this.taskManagerThreadPool = Executors.newFixedThreadPool(buildTaskConf.getMaxExecuteTaskCount());
    }


    /**
     * 获取任务状态
     *
     * @return
     */
    public List<TaskProgress> list() {
        return new ArrayList<>(this.tasks);
    }


    /**
     * 返回成功加入任务队列的项目名
     *
     * @param task
     * @return
     */
    public String[] add(ProjectTask task) {
        final List<ProjectItem> projectItems = Arrays.stream(task.getProjectName()).filter((it) -> {
            return this.projectHelper.getItems().containsKey(it);
        }).map((it) -> {
            return this.projectHelper.getItems().get(it);
        }).collect(Collectors.toList());


        //添加任务到线程中
        TaskProgress taskProgress = new TaskProgress();
        taskProgress.setProjectTask(task);
        taskProgress.setStatus(TaskStatus.Wait);
        taskProgress.setMsg("等待执行");
        taskProgress.setProcess(0);
        tasks.add(taskProgress);

        //任务线程
        ProjectTaskRunnable projectTaskRunnable = this.applicationContext.getBean(ProjectTaskRunnable.class);
        projectTaskRunnable.setTaskProgress(taskProgress);

        //启动任务
        this.taskManagerThreadPool.execute(projectTaskRunnable);

        //返回执行成功的项目名
        return projectItems.stream().map((it) -> {
            return it.getName();
        }).collect(Collectors.toList()).toArray(new String[0]);
    }


    /**
     * 删除任务
     */
    public void removeTask(TaskProgress taskProgress) {
        this.tasks.remove(taskProgress);
    }


}
