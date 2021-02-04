package com.fast.dev.pay.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 消息模版
 */
public interface PayCenterOutputStream {


    public static String name = "PayCenterOutputStream";

    @Output(name)
    MessageChannel messageChannel();


}
