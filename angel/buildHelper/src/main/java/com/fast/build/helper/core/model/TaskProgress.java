package com.fast.build.helper.core.model;


import com.fast.build.helper.core.type.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskProgress {

    //项目
    private ProjectTask projectTask;

    //进度 1 为 100%
    private float process;

    //消息
    private String msg;

    //状态
    private TaskStatus status = TaskStatus.Wait;


}
