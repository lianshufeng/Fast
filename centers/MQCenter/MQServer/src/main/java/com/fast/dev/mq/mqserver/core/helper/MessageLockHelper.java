package com.fast.dev.mq.mqserver.core.helper;

import com.fast.dev.mq.mqserver.core.dao.RedisDao;
import com.fast.dev.mq.mqserver.core.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 消息锁，防止重复消费
 */
@Component
public class MessageLockHelper {

    @Autowired
    private RedisDao redisDao;

    /**
     * 消息锁定
     *
     * @return
     */
    public boolean lock(String messageId) {
        return this.redisDao.setIfAbsent(RedisUtil.buildKey("msg", messageId), "1", 60, TimeUnit.SECONDS);
    }


}
