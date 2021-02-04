package com.fast.build.helper.core.controller;

import com.fast.build.helper.core.helper.ProjectHelper;
import com.fast.build.helper.core.model.ProjectTask;
import com.fast.build.helper.core.model.ProjectTaskResp;
import com.fast.build.helper.core.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("task")
public class TaskController {


    @Autowired
    private ProjectHelper projectHelper;

    @Autowired
    private TaskService taskService;

    /**
     * 执行项目任务
     *
     * @return
     */
    @RequestMapping("add")
    public Object task(ProjectTask task) {
        this.projectHelper.reload();
        if (task.getProjectName() == null || task.getProjectName().length == 0) {
            task.setProjectName(this.projectHelper.getItems().values().stream().filter((it) -> {
                return it != null && it.getBuild() != null && it.getBuild();
            }).map((it) -> {
                return it.getName();
            }).collect(Collectors.toList()).toArray(new String[0]));
        }
        return ProjectTaskResp.builder().projectName(this.taskService.add(task)).createTime(System.currentTimeMillis()).build();
    }


    /**
     * 任务列表
     *
     * @return
     */
    @RequestMapping("list")
    public Object task() {
        return this.taskService.list();
    }
}
