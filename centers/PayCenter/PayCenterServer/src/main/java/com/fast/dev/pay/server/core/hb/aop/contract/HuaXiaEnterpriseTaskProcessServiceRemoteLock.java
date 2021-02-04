package com.fast.dev.pay.server.core.hb.aop.contract;

import com.fast.dev.core.event.aop.MethodAop;
import com.fast.dev.pay.server.core.hb.aop.contract.event.HuaXiaEnterpriseTaskProcessServiceEvent;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HuaXiaEnterpriseTaskProcessServiceRemoteLock extends MethodAop<HuaXiaEnterpriseTaskProcessServiceEvent> {

    @Override
    @Pointcut("execution(public * com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseTaskProcessService.*(..)) && @annotation(org.springframework.transaction.annotation.Transactional)")
    public void methodPoint() {
    }


}
