package com.fast.dev.user.auth.aop;


import com.fast.dev.core.event.aop.MethodAop;
import com.fast.dev.user.auth.event.RoleUpdateEvent;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 权限方法的拦截器
 */
@Aspect
@Component
public class RoleUpdateAop extends MethodAop<RoleUpdateEvent> {


    @Override
    @Pointcut("execution(* com.fast.dev.user.auth.service.RoleService.*(..)) && @annotation(org.springframework.transaction.annotation.Transactional)")
    public void methodPoint() {
    }


}
