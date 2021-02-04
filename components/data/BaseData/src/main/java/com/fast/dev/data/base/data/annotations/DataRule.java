package com.fast.dev.data.base.data.annotations;

import com.fast.dev.data.base.data.type.UpdateType;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * 数据更新规则
 */
@Deprecated
public @interface DataRule {

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
     * 查询条件
     *
     * @return
     */
    String targetQuery();


    /**
     * 更新类型，默认为替换, inc只支持数字类型
     */
    UpdateType updateType() default UpdateType.Replace;


}
