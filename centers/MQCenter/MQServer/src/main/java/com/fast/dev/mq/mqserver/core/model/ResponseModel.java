package com.fast.dev.mq.mqserver.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求的模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseModel {


    /**
     * 请求的地址
     */
    private String msgId;

    /**
     * 响应编码
     */
    private int code;

    /**
     * 本体
     */
    private Object body;


}
