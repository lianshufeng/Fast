package com.fast.dev.mq.mqserver.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendModel {

    /**
     * 订阅
     */
    private String topic;

    /**
     * 参数
     */
    private Object parm;

}
