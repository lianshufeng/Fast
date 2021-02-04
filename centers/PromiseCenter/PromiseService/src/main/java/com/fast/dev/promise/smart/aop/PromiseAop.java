package com.fast.dev.promise.smart.aop;

import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.smart.helper.SmartHelper;
import lombok.extern.java.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log
@Aspect
@Component
public class PromiseAop {

    @Autowired
    private SmartHelper smartHelper;


    @Pointcut("@annotation(com.fast.dev.promise.smart.annotations.PromiseTask) ")
    public void promiseMethodAop() {
    }


    @Around("promiseMethodAop()")
    public Object aroundCacheAop(ProceedingJoinPoint pjp) {
        return execute(pjp);
    }


    /**
     * 执行任务
     *
     * @param pjp
     * @return
     */
    private Object execute(ProceedingJoinPoint pjp) {
        SmartHelper.TaskHandle handle = null;
        //获取请求参数
        RequestParmModel requestParmModel = getRequestParmModel(pjp);
        if (requestParmModel != null) {
            handle = smartHelper.build(requestParmModel);
        }
        //执行原方法
        Object o = null;
        try {
            o = pjp.proceed();
            handle.remove();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            //立即执行方法
            handle.doit();
            throw  new RuntimeException(throwable);
        }
        return o;

    }


    /**
     * 通过参数找到该实现
     *
     * @param pjp
     * @return
     */
    private RequestParmModel getRequestParmModel(ProceedingJoinPoint pjp) {
        //遍历所有参数
        for (Object arg : pjp.getArgs()) {
            if (arg instanceof RequestParmModel) {
                return (RequestParmModel) arg;
            }
        }
        return null;
    }


}