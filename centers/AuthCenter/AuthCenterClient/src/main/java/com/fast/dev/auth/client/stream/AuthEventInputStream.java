package com.fast.dev.auth.client.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface AuthEventInputStream {


    public static String name = "AuthEventInputStream";

    /**
     * 订阅
     *
     * @return
     */
    @Input(name)
    SubscribableChannel input();


}
