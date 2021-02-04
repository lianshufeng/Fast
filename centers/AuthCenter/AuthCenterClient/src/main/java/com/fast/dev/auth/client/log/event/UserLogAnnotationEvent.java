package com.fast.dev.auth.client.log.event;

import com.fast.dev.core.event.method.MethodEvent;

public class UserLogAnnotationEvent extends MethodEvent {
    /**
     * 构造方法
     *
     * @param source
     */
    public UserLogAnnotationEvent(Source source) {
        super(source);
    }
}
