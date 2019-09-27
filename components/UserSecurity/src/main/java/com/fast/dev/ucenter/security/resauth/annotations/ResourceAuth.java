package com.fast.dev.ucenter.security.resauth.annotations;

import com.fast.dev.ucenter.security.resauth.type.ResourceScopeType;

import java.lang.annotation.*;

/**
 * 资源权限注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ResourceAuth {


    /**
     * 资源名称
     *
     * @return
     */
    String value();


    /**
     * 描述
     *
     * @return
     */
    String remark() default "";


    /**
     * 是否需要鉴权
     *
     * @return
     */
    ResourceScopeType scopeType() default ResourceScopeType.NeedAuth;

}
