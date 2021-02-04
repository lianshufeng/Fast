package com.fast.dev.promise.model;

import com.fast.dev.core.util.net.apache.HttpModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

/**
 * 请求的模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class RequestParmModel {


    /**
     * 任务id
     */
    private String id;


    /**
     * 执行时间
     */
    private Long executeTime;


    /**
     * http的任务
     */
    private HttpModel http;


    /**
     * 错误模型
     */
    private ErrorTryModel errorTry;


}
