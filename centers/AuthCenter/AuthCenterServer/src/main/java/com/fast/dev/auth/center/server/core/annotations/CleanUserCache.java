package com.fast.dev.auth.center.server.core.annotations;


import com.fast.dev.auth.center.server.core.type.CleanUserCacheType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户缓存注解
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CleanUserCache {


    /**
     * 表达式
     *
     * @return
     */
    String value() default "";


    /**
     * 类型
     *
     * @return
     */
    CleanUserCacheType type();


}
