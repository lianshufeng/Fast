package com.fast.dev.core.event.method;

import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.lang.reflect.Method;

public abstract class MethodEvent extends ApplicationEvent {

    /**
     * 构造方法
     *
     * @param source
     */
    public MethodEvent(MethodEvent.Source source) {
        super(source);
    }


    /**
     * 获取传递的参数对象
     *
     * @return
     */
    public MethodEvent.Source getSource() {
        return (MethodEvent.Source) super.getSource();
    }


    @Data
    @Builder
    @ToString(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Source {

        //方法名
        private String methodName;

        //具体的方法
        private Method method;

        //呼叫类型
        private MethodEvent.CallType callType;

        //参数
        private Object[] parm;

        //参数名
        private String[] parmName;

        //返回的内容
        private Object ret;

        //异常
        private Throwable throwable;


    }

    /**
     * 调用参数
     */
    public enum CallType {
        Before, After
    }

}
