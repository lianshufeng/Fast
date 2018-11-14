package com.fast.dev.pushcenter.manager.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 消息模版
 */
public interface PushCenterOuputStream {


    public static String name = "PushCenterOuputStream";

    @Output(name)
    MessageChannel messageChannel();


}
