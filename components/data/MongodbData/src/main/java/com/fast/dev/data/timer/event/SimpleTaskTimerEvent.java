package com.fast.dev.data.timer.event;

import com.fast.dev.data.mongo.domain.SuperEntity;

@FunctionalInterface
public interface SimpleTaskTimerEvent<T extends SuperEntity> {

    /**
     * 执行
     *
     * @param entity
     */
    void execute(T entity);
}
