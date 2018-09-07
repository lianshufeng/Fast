package com.fast.dev.ucenter.core.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.ucenter.core.dao.UserTokenDao;
import com.fast.dev.ucenter.core.domain.BaseToken;
import com.fast.dev.ucenter.core.domain.ServiceToken;
import com.fast.dev.ucenter.core.domain.StrongServiceToken;
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
     * @param baseToken
     * @param expireTime
     */
    private void createToken(String key, BaseToken baseToken, long expireTime) {
        ValueOperations valueOperations = this.redisTemplate.opsForValue();
        valueOperations.set(key, baseToken, expireTime, TimeUnit.MILLISECONDS);
        //设置到期时间
        setExpireTime(baseToken);
    }


    @Override
    public boolean createUserToken(UserToken userToken, long expireTime) {
        createToken(userToken.getToken(), userToken, expireTime);
        return true;
    }

    @Override
    public boolean createServiceToken(ServiceToken serviceToken, long expireTime) {
        createToken(serviceToken.getToken(), serviceToken, expireTime);
        return true;
    }

    @Override
    public boolean createStrongServiceToken(StrongServiceToken strongServiceToken, long expireTime) {
        createToken(strongServiceToken.getToken(), strongServiceToken, expireTime);
        return true;
    }

    @Override
    public <T extends BaseToken> T query(String token) {
        // 访问次数+1 并入库
        BaseToken baseToken = queryOnly(token);
        if (baseToken == null) {
            return null;
        }
        baseToken.setAccessCount(baseToken.getAccessCount() + 1);
        baseToken.setUpdateTime(dbHelper.getTime());
        ValueOperations valueOperations = this.redisTemplate.opsForValue();
        valueOperations.set(token, baseToken);
        return (T) baseToken;
    }


    @Override
    public <T extends BaseToken> T queryOnly(String token) {
        ValueOperations valueOperations = this.redisTemplate.opsForValue();
        BaseToken baseToken = (BaseToken) valueOperations.get(token);
        if (baseToken == null) {
            return null;
        }
        //设置到期时间
        setExpireTime(baseToken);
        return (T) baseToken;
    }

    @Override
    public boolean remove(String token) {
        ValueOperations valueOperations = this.redisTemplate.opsForValue();
        return this.redisTemplate.delete(token);
    }


    /**
     * 设置到期时间
     * @param baseToken
     */
    private void setExpireTime(BaseToken baseToken){
        baseToken.setExpireTime(this.redisTemplate.getExpire(baseToken.getToken(), TimeUnit.MILLISECONDS));
    }
}
