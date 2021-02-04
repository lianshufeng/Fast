package com.fast.dev.mq.mqserver.core.service;

import com.fast.dev.mq.mqserver.core.conf.MQConf;
import com.fast.dev.mq.mqserver.core.dao.RedisDao;
import com.fast.dev.mq.mqserver.core.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

//@Service
public class TokenService {

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private MQConf mqConf;

    //令牌的前缀
    private final static String TokenName = "tk";


    /**
     * 修改token
     *
     * @param token
     * @return
     */
    public boolean update(String token) {
        if (!exist(token)) {
            return false;
        }
        this.redisDao.set(RedisUtil.buildKey(TokenName, token), "1", mqConf.getTokenLifeTime(), TimeUnit.MILLISECONDS);
        return true;
    }


    /**
     * 添加令牌
     *
     * @param token
     * @return
     */
    public boolean add(String token) {
        return this.redisDao.setIfAbsent(RedisUtil.buildKey(TokenName, token), "1", mqConf.getTokenLifeTime(), TimeUnit.MILLISECONDS);
    }


    /**
     * 存在
     *
     * @param token
     * @return
     */
    public boolean exist(String token) {
        return this.redisDao.size(RedisUtil.buildKey(TokenName, token)) > 0;
    }

}
