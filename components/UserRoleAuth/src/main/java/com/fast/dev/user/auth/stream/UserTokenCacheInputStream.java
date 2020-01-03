package com.fast.dev.user.auth.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 消息模版
 */
public interface UserTokenCacheInputStream {



    public static String name="UserTokenCacheInputStream";

    /**
     * 订阅
     *
     * @return
     */
    @Input(name)
    SubscribableChannel input();




}
