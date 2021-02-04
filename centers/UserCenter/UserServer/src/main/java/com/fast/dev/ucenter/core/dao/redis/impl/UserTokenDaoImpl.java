package com.fast.dev.ucenter.core.dao.redis.impl;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.ucenter.core.dao.redis.UserTokenDao;
import com.fast.dev.ucenter.core.domain.BaseToken;
import com.fast.dev.ucenter.core.domain.ServiceToken;
import com.fast.dev.ucenter.core.domain.StrongServiceToken;
import com.fast.dev.ucenter.core.domain.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 作者：练书锋
 * 时间：20190622
 */

@Repository
public class UserTokenDaoImpl implements UserTokenDao {


    //缓存字符串与class的关系
    private Map<String, Class> classCache = new ConcurrentHashMap<>();

    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
        ValueOperations valueOperations = this.stringRedisTemplate.opsForValue();
        //创建数据令牌
        valueOperations.set(key, serialization(baseToken), expireTime, TimeUnit.MILLISECONDS);
        //创建计次令牌
        valueOperations.set(buildAccessKey(key), "0", expireTime, TimeUnit.MILLISECONDS);
        //设置到期时间
        setBaseTokenExpireTime(baseToken);
    }


    @Override
    public boolean createUserToken(UserToken userToken, long expireTime) {

        String token = userToken.getToken();

        //记录到缓存的令牌
        createToken(token, userToken, expireTime);

        //缓存当前用户
        ValueOperations valueOperations = this.stringRedisTemplate.opsForValue();
        valueOperations.set((buildUserKey(userToken.getUid()) + token), token, expireTime, TimeUnit.MILLISECONDS);


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

        //访问计次
        this.stringRedisTemplate.opsForValue().increment(buildAccessKey(baseToken.getToken()), 1);

        return (T) baseToken;
    }


    @Override
    public <T extends BaseToken> T queryOnly(String token) {
        ValueOperations valueOperations = this.stringRedisTemplate.opsForValue();
        Object val = valueOperations.get(token);
        if (val == null) {
            return null;
        }
        BaseToken baseToken = unSerialization(String.valueOf(val));
        if (baseToken == null) {
            return null;
        }
        //设置到期时间
        setBaseTokenExpireTime(baseToken);

        //设置访问次数
        setAccessCount(baseToken);

        return (T) baseToken;
    }

    @Override
    public boolean remove(String token) {
        BaseToken baseToken = unSerialization(this.stringRedisTemplate.opsForValue().get(token));

        //删除用户令牌
        if (baseToken != null && baseToken instanceof UserToken) {
            UserToken userToken = (UserToken) baseToken;
            this.stringRedisTemplate.delete(buildUserKey(userToken.getUid()) + token);
        }

        //删除访问计次token
        if (baseToken != null) {
            this.stringRedisTemplate.delete(buildAccessKey(token));
        }

        return this.stringRedisTemplate.delete(token);
    }

    @Override
    public boolean removeFromUid(String uid) {
        if (uid != null) {
            //用户_令牌
            String userTokenKey = buildUserKey(uid);

            //批量删除
            Set<String> user_token = this.stringRedisTemplate.keys("*" + userTokenKey + "*");

            //取出token
            Set<String> uTokens = new HashSet<>();
            Set<String> accessTokens = new HashSet<>();
            for (String userToken : user_token) {
                String uToken = userToken.substring(userTokenKey.length(), userToken.length());
                uTokens.add(uToken);
                accessTokens.add(buildAccessKey(uToken));
            }

            this.stringRedisTemplate.delete(uTokens);
            this.stringRedisTemplate.delete(accessTokens);
            this.stringRedisTemplate.delete(user_token);

            return true;
        }
        return false;
    }


    /**
     * 设置到期时间
     *
     * @param baseToken
     */
    private void setBaseTokenExpireTime(BaseToken baseToken) {
        baseToken.setExpireTime(this.stringRedisTemplate.getExpire(baseToken.getToken(), TimeUnit.MILLISECONDS));
    }


    /**
     * 设置访问次数
     *
     * @param baseToken
     */
    private void setAccessCount(BaseToken baseToken) {
        String count = this.stringRedisTemplate.opsForValue().get(buildAccessKey(baseToken.getToken()));
        baseToken.setAccessCount(Long.parseLong(count));
    }


    @Override
    public Set<BaseToken> findByUid(String uid) {
        Set<String> keys = this.stringRedisTemplate.keys("*" + buildUserKey(uid) + "*");
        if (keys != null) {
            List<String> query = this.stringRedisTemplate.opsForValue().multiGet(keys);
            Set<BaseToken> baseTokens = new HashSet<>();
            ValueOperations valueOperations = this.stringRedisTemplate.opsForValue();
            for (Object json : valueOperations.multiGet(query)) {
                baseTokens.add(unSerialization(String.valueOf(json)));
            }
            return baseTokens;
        }
        return null;
    }


    /**
     * 构建用户的缓存key
     *
     * @param uid
     * @return
     */
    private String buildUserKey(String uid) {
        return "user_" + uid + "_";
    }


    /**
     * 创建访问次数token
     *
     * @param token
     * @return
     */
    private String buildAccessKey(String token) {
        return "access_" + token;
    }


    /**
     * 实体对象转换为json字符串
     *
     * @param baseToken
     * @return
     */
    private String serialization(BaseToken baseToken) {
        //类名 : 数据实体
        return baseToken.getClass().getName() + ":" + JsonUtil.toJson(baseToken);
    }


    /**
     * 字符串转换为对象
     *
     * @param ret
     * @return
     */
    private <T> T unSerialization(String ret) {
        if (StringUtils.isEmpty(ret)) {
            return null;
        }

        int at = ret.indexOf(":");
        try {
            String className = ret.substring(0, at);
            String json = ret.substring(at + 1);
            Class cls = getEntityClass(className);
            return (T) JsonUtil.toObject(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取出class
     *
     * @param className
     * @return
     */
    private Class getEntityClass(String className) {
        Class cls = this.classCache.get(className);
        if (cls == null) {
            try {
                cls = Class.forName(className);
                this.classCache.put(className, cls);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return cls;
    }

}
