package com.fast.dev.ucenter.core.service;

import com.fast.dev.data.mongo.domain.SuperEntity;

public interface LocalUserService {
    /**
     * 删除过期数据记录
     *
     * @param entityCls
     * @param time
     * @return
     */
    long cleanTimeOutRecord(Class<? extends SuperEntity> entityCls, long time);
}
