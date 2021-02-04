package com.fast.dev.auth.center.server.core.aop;

import com.fast.dev.auth.center.server.core.event.AuthPublishEvent;
import com.fast.dev.core.event.aop.MethodAop;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 权限发布事件的AOP
 */
@Aspect
@Component
public class AuthPublishEventAop extends MethodAop<AuthPublishEvent> {
    @Override
    @Pointcut("@annotation(com.fast.dev.auth.center.server.core.annotations.AuthEvent) ")
    public void methodPoint() {

    }
}
