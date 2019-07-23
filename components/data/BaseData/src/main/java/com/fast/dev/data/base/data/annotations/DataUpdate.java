package com.fast.dev.data.base.data.annotations;


import java.lang.annotation.*;


/**
 * 用于实体数据人同步操作，简化冗余数据的复杂度
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataUpdate {

    /**
     * 数据规则
     *
     * @return
     */
    DataRule[] value();


}
