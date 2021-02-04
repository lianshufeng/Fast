package com.fast.dev.data.base.data;

/**
 * 数据清洗任务
 */
public interface DataCleanTask<T> {


    /**
     * 任务名,不允许重复
     *
     * @return
     */
    String taskName();

    /**
     * 每次拉取的数量
     *
     * @return
     */
    int batchSize();


    /**
     * 数据清洗
     *
     * @param ts
     */
    void clean(T[] ts);


    /**
     * 任务开始执行
     */
    void onStart();

    /**
     * 任务结束执行
     */
    void onEnd();


}
