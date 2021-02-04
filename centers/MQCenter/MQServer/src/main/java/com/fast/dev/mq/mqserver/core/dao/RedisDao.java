package com.fast.dev.mq.mqserver.core.dao;

import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class RedisDao {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private void init() {
        this.kvValueOperations = stringRedisTemplate.opsForValue();
    }


    @Delegate(types = ValueOperations.class)
    private ValueOperations kvValueOperations;


//    private interface RedisTemplateProxyMethod {
//
//        void set(String key, String value, long timeout, TimeUnit unit);
//
//        Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit);
//
//        Long size(String key);
//
//    }


}
