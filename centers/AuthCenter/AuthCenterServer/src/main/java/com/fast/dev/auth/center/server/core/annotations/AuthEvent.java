package com.fast.dev.auth.center.server.core.annotations;

import com.fast.dev.auth.client.type.AuthEventAction;
import com.fast.dev.auth.client.type.AuthEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthEvent {

    /**
     * 表达式,可用EL表达式，返回对象 #ret
     *
     * @return
     */
    String filter() default "#ret";


    /**
     * 事件类型
     */
    AuthEventType type();


    /**
     * 事件动作
     */
    AuthEventAction action();


    /**
     * EL表达式 #parm
     *
     * @return
     */
    String[] parm();


}
