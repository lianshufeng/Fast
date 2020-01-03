package com.fast.dev.user.auth.event;

import com.fast.dev.core.event.method.MethodEvent;

/**
 * 角色更新的事件
 */
public class RoleUpdateEvent extends MethodEvent {


    /**
     * 构造方法
     *
     * @param source
     */
    public RoleUpdateEvent(Source source) {
        super(source);
    }
}
