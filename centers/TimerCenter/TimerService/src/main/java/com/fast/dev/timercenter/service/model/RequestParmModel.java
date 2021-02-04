package com.fast.dev.timercenter.service.model;

import com.fast.dev.core.util.net.apache.HttpModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;


/**
 * 请求的模型
 */
@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class RequestParmModel {


    /**
     * 任务id
     */
    private String taskId;


    /**
     * 补偿次数，至少大于1
     */
    private Integer tryCount;


    /**
     * 执行延迟时间，单位秒
     */
    private Long delayTime;


    /**
     * http的任务
     */
    private HttpModel http = new HttpModel();


    /**
     * 补偿模型
     */
    private CheckModel check = new CheckModel();


}
