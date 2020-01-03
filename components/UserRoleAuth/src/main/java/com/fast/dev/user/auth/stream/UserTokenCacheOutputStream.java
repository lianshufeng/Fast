package com.fast.dev.user.auth.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 消息模版
 */
public interface UserTokenCacheOutputStream {


    public static String name = "UserTokenCacheOutputStream";

    @Output(name)
    MessageChannel messageChannel();


}
