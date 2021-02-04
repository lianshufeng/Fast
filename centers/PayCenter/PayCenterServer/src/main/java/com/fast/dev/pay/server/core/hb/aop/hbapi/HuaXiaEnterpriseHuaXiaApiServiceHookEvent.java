package com.fast.dev.pay.server.core.hb.aop.hbapi;

import com.fast.dev.core.event.method.MethodEvent;

public class HuaXiaEnterpriseHuaXiaApiServiceHookEvent extends MethodEvent {
    /**
     * 构造方法
     *
     * @param source
     */
    public HuaXiaEnterpriseHuaXiaApiServiceHookEvent(Source source) {
        super(source);
    }
}
