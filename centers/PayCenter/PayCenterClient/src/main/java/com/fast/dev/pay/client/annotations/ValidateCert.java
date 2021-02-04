package com.fast.dev.pay.client.annotations;

import java.lang.annotation.*;

/**
 * 需要校验证书
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD})
@Documented
@Inherited  //可以继承
public @interface ValidateCert {
}
