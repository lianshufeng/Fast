package com.fast.dev.auth.center.server.core.event;

import com.fast.dev.core.event.method.MethodEvent;

public class AuthPublishEvent extends MethodEvent {
    /**
     * 构造方法
     *
     * @param source
     */
    public AuthPublishEvent(Source source) {
        super(source);
    }
}
