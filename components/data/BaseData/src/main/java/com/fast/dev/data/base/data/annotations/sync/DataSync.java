package com.fast.dev.data.base.data.annotations.sync;


import com.fast.dev.data.base.data.DataCleanCondition;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.lang.annotation.*;

/**
 * 数据同步注解
 */

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = DataSyncs.class)
public @interface DataSync {

    /**
     * 目标实体
     *
     * @return
     */
    Class<? extends AbstractPersistable> targetEntity();


    /**
     * 目标的字段名
     */
    String targetField();

    /**
     * 查询条件,可用
     *
     * @return
     */
    String targetQuery();


    /**
     * 原数据成为变量
     *
     * @return
     */
    String targetExpression() default "";


    /**
     * 触发更新条件,多个条件用and
     *
     * @return
     */
    Class<? extends DataCleanCondition>[] condition() default {};

}
