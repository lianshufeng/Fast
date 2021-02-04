package com.fast.dev.ucenter.security.resauth.annotations;

import java.lang.annotation.*;

/**
 * 资源权限注解
 */

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ResourceAuths {

    ResourceAuth[] value();


}
