package com.fast.dev.data.base.data;

/**
 * 数据清洗的条件
 */
public interface DataCleanCondition<T> {


    /**
     * 数据可否清洗的规则
     *
     * @param id
     * @return
     */
    boolean check(T id);

}
