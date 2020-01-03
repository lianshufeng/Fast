package com.fast.dev.user.auth.aop;

import com.fast.dev.core.event.aop.MethodAop;
import com.fast.dev.user.auth.event.UserAuthEvent;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 用户身份的aop
 */
@Aspect
@Component
public class UserAuthenticationAop extends MethodAop<UserAuthEvent> {


    @Override
    @Pointcut("execution(* com.fast.dev.ucenter.security.service.UserAuthentication.authentication(..))")
    public void methodPoint() {

    }


}
