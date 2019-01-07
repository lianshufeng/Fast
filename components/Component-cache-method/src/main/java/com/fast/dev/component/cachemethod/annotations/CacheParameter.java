package com.fast.dev.component.cachemethod.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 本注解需要结合 CacheMethod 与 CacheUpdate
 * 
 * @author 练书锋
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheParameter {

}
