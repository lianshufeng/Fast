package com.fast.dev.ucenter.core.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 消息模版
 */
public interface UserCenterInputStream {



    public static String name="UserCenterInputStream";

    /**
     * 订阅
     *
     * @return
     */
    @Input(name)
    SubscribableChannel input();




}
