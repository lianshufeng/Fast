package com.fast.dev.promise.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStatusModel {


    /**
     * 下次执行任务的时间
     */
    private long nextExecuteTime;


}
