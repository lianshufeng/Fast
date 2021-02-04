package com.fast.dev.timercenter.service.model;

import com.fast.dev.timercenter.service.type.TaskState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class TaskModel extends RequestParmModel {

    //心跳时间
    private Long heartbeatTime;


}
