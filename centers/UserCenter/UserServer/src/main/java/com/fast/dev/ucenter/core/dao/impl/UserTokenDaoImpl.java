package com.fast.dev.ucenter.core.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.ucenter.core.dao.UserTokenDao;
import com.fast.dev.ucenter.core.domain.BaseToken;
import com.fast.dev.ucenter.core.domain.ServiceToken;
import com.fast.dev.ucenter.core.domain.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 */

@Repository
public class UserTokenDaoImpl implements UserTokenDao {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DBHelper dbHelper;


    /**
     * 写DB
     *
     * @param key
     * @param token
     * @param timeOut
     */
    private void createToken(String key, Serializable token, long timeOut) {
        ValueOperations valueOperations = this.redisTemplate.opsForValue();
        valueOperations.set(key, token, timeOut, TimeUnit.MILLISECONDS);
    }


    @Override
    public boolean createUserToken(UserToken userToken, long timeOut) {
        createToken(userToken.getToken(), userToken, timeOut);
        return true;
    }

    @Override
    public boolean createServiceToken(ServiceToken serviceToken,  long timeOut) {
        createToken(serviceToken.getToken(), serviceToken, timeOut);
        return true;
    }

    @Override
    public <T extends BaseToken> T query(String token) {
        ValueOperations valueOperations = this.redisTemplate.opsForValue();
        Object o = valueOperations.get(token);
        if (o == null) {
            return null;
        }

        // 访问次数+1 并入库
        BaseToken baseToken = (BaseToken) o;
        baseToken.setAccessCount(baseToken.getAccessCount() + 1);
        baseToken.setUpdateTime(dbHelper.getTime());
        valueOperations.set(token, baseToken);

        return (T) baseToken;
    }

    @Override
    public boolean remove(String token) {
        ValueOperations valueOperations = this.redisTemplate.opsForValue();
        return this.redisTemplate.delete(token);
    }
}
