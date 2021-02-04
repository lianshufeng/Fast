package com.fast.dev.pay.server.core.hb.aop.hbapi;

import com.fast.dev.core.event.aop.MethodAop;
import com.fast.dev.pay.server.core.hb.aop.contract.event.HuaXiaEnterpriseTaskProcessServiceEvent;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HuaXiaEnterpriseHuaXiaApiServiceHook extends MethodAop<HuaXiaEnterpriseHuaXiaApiServiceHookEvent> {

    @Override
    @Pointcut("execution(public * com.fast.dev.pay.server.core.hb.service.HuaXiaApiService.*(..)) ")
    public void methodPoint() {
    }


}
