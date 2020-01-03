package com.fast.dev.user.auth.event;

import com.fast.dev.core.event.method.MethodEvent;

/**
 * 用户授权访问的事件
 */
public class UserAuthEvent extends MethodEvent {


    /**
     * 构造方法
     *
     * @param source
     */
    public UserAuthEvent(Source source) {
        super(source);
    }
}
