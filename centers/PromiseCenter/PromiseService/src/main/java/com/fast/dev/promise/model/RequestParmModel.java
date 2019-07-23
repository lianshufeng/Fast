package com.fast.dev.promise.model;

import com.fast.dev.core.util.net.apache.HttpModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Max;

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
    @Max(value = 1000 * 60 * 60 * 24, message = "执行时间不能超过1天")
    private Long executeTime;


    /**
     * http的任务
     */
    @Valid
    private HttpModel http;


    /**
     * 错误模型
     */
    @Valid
    private ErrorTryModel errorTry;


}
