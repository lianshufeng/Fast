package com.fast.dev.auth.center.server.core.event;

import com.fast.dev.core.event.method.MethodEvent;

public class CleanUserCacheEvent extends MethodEvent {
    /**
     * 构造方法
     *
     * @param source
     */
    public CleanUserCacheEvent(Source source) {
        super(source);
    }
}
