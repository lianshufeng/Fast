package com.fast.dev.auth.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 消息模版
 */
public interface UserLogOutputStream {


    public static String name = "UserLogOutputStream";

    @Output(name)
    MessageChannel messageChannel();


}
