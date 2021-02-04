package com.fast.dev.ucenter.core.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 消息模版
 */
public interface UserCenterOuputStream {


    public static String name = "UserCenterOuputStream";

    @Output(name)
    MessageChannel messageChannel();


}
