package com.fast.dev.auth.client.log.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 用户日志
 */

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface UserLog {

    @AliasFor("action")
    String value() default "";

    /**
     * 行为
     *
     * @return
     */
    String action() default "";

    /**
     * 参数
     *
     * @return
     */
    String[] parameter() default {};

}
