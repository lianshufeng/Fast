package com.fast.dev.auth.client.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface AuthEventOutputStream {


    public static String name = "AuthEventOutputStream";

    @Output(name)
    MessageChannel messageChannel();

}
