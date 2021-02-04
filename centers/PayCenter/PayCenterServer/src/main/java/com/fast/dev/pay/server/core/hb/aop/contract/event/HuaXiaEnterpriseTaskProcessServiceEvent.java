package com.fast.dev.pay.server.core.hb.aop.contract.event;

import com.fast.dev.core.event.method.MethodEvent;

public class HuaXiaEnterpriseTaskProcessServiceEvent extends MethodEvent {
    /**
     * 构造方法
     *
     * @param source
     */
    public HuaXiaEnterpriseTaskProcessServiceEvent(Source source) {
        super(source);
    }
}
