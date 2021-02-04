package com.fast.dev.timercenter.service.model;

import lombok.*;

@Data
@Builder
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStatusModel {


    /**
     * 下次执行任务的时间
     */
    private long nextExecuteTime;


}
