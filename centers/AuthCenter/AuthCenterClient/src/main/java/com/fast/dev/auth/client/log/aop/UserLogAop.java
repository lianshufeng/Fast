package com.fast.dev.auth.client.log.aop;

import com.fast.dev.auth.client.log.event.UserLogAnnotationEvent;
import com.fast.dev.core.event.aop.MethodAop;
import groovy.util.logging.Log;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 权限钩子：用于扩展权限的缓存时间
 */
@Log
@Aspect
@Component
public class UserLogAop extends MethodAop<UserLogAnnotationEvent> {

    @Override
    @Pointcut("@annotation(com.fast.dev.auth.client.log.annotations.UserLog) ")
    public void methodPoint() {

    }

}
