package com.fast.dev.gateway.core.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Repository
public class BlacklistDao extends SuperRedisDao {


    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 增加到黑名单表里
     *
     * @param ttl
     * @param ip
     * @param roleName
     */
    public void append(long ttl, String ip, String roleName) {
        final String key = buildKey(ip, roleName);
        this.redisTemplate.opsForValue().set(key, "0", ttl, TimeUnit.SECONDS);
    }


    /**
     * 是否存在黑名单里
     *
     * @param ip
     * @param roleName
     * @return
     */
    public boolean exitsBlacklist(String ip, String roleName) {
        final String key = buildKey(ip, roleName);
        if (!StringUtils.hasText(this.redisTemplate.opsForValue().get(key))) {
            return false;
        }
        this.redisTemplate.opsForValue().increment(key);
        return true;
    }

    @Override
    public String tableName() {
        return "bl";
    }
}
