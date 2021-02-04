package com.fast.dev.core.event.aop;

import com.fast.dev.core.event.method.MethodEvent;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 实
 */
public abstract class MethodAop<T extends MethodEvent> {


    @Autowired
    private ApplicationContext applicationContext;

    //构造方法
    private Constructor constructor;


    @Autowired
    @SneakyThrows
    private void init() {
        Type superType = getClass().getGenericSuperclass();//如果使用继承
        Type daoType = ((ParameterizedType) superType).getActualTypeArguments()[0];
        constructor = ((Class) daoType).getDeclaredConstructors()[0];
    }


    /**
     * aop的切入点
     */
    public abstract void methodPoint();


    @Around(value = "methodPoint()")
    public Object afterMethod(ProceedingJoinPoint point) throws Throwable {

        //方法执行的异常
        Throwable methodThrowable = null;

        //aop执行之前
        before(point);

        //执行代码
        Object ret = null;
        try {
            ret = point.proceed();
        } catch (Throwable throwable) {
            methodThrowable = throwable;
            throwable.printStackTrace();
            throw throwable;
        } finally {
            //代码执行之后
            after(point, ret, methodThrowable);
        }
        return ret;
    }

    private void before(ProceedingJoinPoint point) {
        applicationContext.publishEvent(buildMethodEvent(point, MethodEvent.CallType.Before, point.getArgs()));
    }


    private void after(ProceedingJoinPoint point, Object ret, Throwable throwable) {
        //执行后
        MethodEvent methodEvent = buildMethodEvent(point, MethodEvent.CallType.After, point.getArgs());
        methodEvent.getSource().setRet(ret);
        methodEvent.getSource().setThrowable(throwable);
        applicationContext.publishEvent(methodEvent);
    }


    /**
     * 创建角色的事件
     *
     * @param point
     * @return
     */
    @SneakyThrows
    private MethodEvent buildMethodEvent(JoinPoint point, MethodEvent.CallType callType, Object[] parm) {

        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        MethodEvent.Source source = new MethodEvent.Source();

        //方法
        source.setMethod(method);
        source.setMethodName(method.getName());

        //调用的方法类型：之前或者之后
        source.setCallType(callType);

        //传递的参数
        source.setParm(parm);
        source.setParmName(methodSignature.getParameterNames());


        //具体的实现的实例
        Object newInstance = constructor.newInstance(source);
        return (MethodEvent) newInstance;
    }


}
