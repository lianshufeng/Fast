package com.fast.dev.user.auth.aop;

import com.fast.dev.core.event.aop.MethodAop;
import com.fast.dev.user.auth.event.UserRequestEvent;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 判断返回值增加用户的缓存数据
 */
@Aspect
@Component
public class UserRequestAop extends MethodAop<UserRequestEvent> {

    @Override
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) ")
    public void methodPoint() {

    }
}
