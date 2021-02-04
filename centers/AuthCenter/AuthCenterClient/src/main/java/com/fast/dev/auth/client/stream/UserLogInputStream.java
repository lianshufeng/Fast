package com.fast.dev.auth.client.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 消息模版
 */
public interface UserLogInputStream {


    public static String name = "UserLogInputStream";

    /**
     * 订阅
     *
     * @return
     */
    @Input(name)
    SubscribableChannel input();


}
