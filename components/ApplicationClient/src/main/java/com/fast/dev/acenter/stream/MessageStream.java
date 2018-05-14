package com.fast.dev.acenter.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

/**
 * 消息流
 */
public abstract class MessageStream<T> {


    /**
     * 名称
     *
     * @return
     */
    public abstract String name();


    /**
     * 发布
     *
     * @param entity
     */
    public void publish(T entity) {

    }

    /**
     * 订阅
     *
     * @param entity
     */
    @Input
    public abstract void subscribe(T entity);


}
