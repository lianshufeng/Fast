package com.fast.dev.data.base.data;

/**
 * 数据清洗管理器
 */
public interface DataCleanManager {

    /**
     * 开始数据清洗
     *
     * @param taskName
     * @return
     */
    boolean execute(String taskName);


    /**
     * 开始数据清洗
     *
     * @param taskName
     * @return
     */
    boolean execute(String taskName, boolean all);


}
