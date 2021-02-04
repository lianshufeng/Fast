package com.fast.dev.pay.client.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 消息模版
 */
public interface PayCenterInputStream {


    public static String name = "PayCenterInputStream";

    /**
     * 订阅
     *
     * @return
     */
    @Input(name)
    SubscribableChannel input();


}
