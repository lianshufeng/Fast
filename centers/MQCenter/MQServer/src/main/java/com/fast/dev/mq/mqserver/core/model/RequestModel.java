package com.fast.dev.mq.mqserver.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * 请求的模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class RequestModel {


    /**
     * 请求的地址
     */
    private String url;


    /**
     * 请求头
     */
    private Map<String, String> header;


    /**
     * 参数
     */
    private Map<String, Object> parameter;


    /**
     * 是否json格式
     */
    private boolean json;


    /**
     * 响应管道
     */
    private String token;


    /**
     * 消息id
     */
    private String msgId;


    /**
     * 超时时间
     */
    private long timeOut = 30000l;

}
