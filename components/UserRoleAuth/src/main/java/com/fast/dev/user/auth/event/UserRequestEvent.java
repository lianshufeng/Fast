package com.fast.dev.user.auth.event;

import com.fast.dev.core.event.method.MethodEvent;

public class UserRequestEvent extends MethodEvent {
    /**
     * 构造方法
     *
     * @param source
     */
    public UserRequestEvent(Source source) {
        super(source);
    }
}
