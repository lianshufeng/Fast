package com.fast.dev.promise.smart.annotations;

import java.lang.annotation.*;

/**
 * 任务注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PromiseTask {

}
