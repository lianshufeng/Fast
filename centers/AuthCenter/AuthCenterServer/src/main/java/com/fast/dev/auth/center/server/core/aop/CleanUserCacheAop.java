package com.fast.dev.auth.center.server.core.aop;

import com.fast.dev.auth.center.server.core.event.CleanUserCacheEvent;
import com.fast.dev.core.event.aop.MethodAop;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CleanUserCacheAop extends MethodAop<CleanUserCacheEvent> {

    @Override
    @Pointcut("@annotation(com.fast.dev.auth.center.server.core.annotations.CleanUserCache) ")
    public void methodPoint() {
    }


}
