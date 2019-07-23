package com.fast.dev.promise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskModel extends RequestParmModel {

    /**
     * 系统时间
     */
    private long heartbeatTime;


}
