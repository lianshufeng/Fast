package com.fast.dev.ucenter.security.resauth.annotations;

import java.lang.annotation.*;

/**
 * 资源权限注解
 */

@Target({ElementType.METHOD, ElementType.TYPE,ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Repeatable(value = ResourceAuths.class)
public @interface ResourceAuth {


    /**
     * 资源名称
     *
     * @return
     */
    String[] value();


    /**
     * 描述
     *
     * @return
     */
    String remark() default "";


}
