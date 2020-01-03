package com.fast.dev.ucenter.security.cache;

import com.fast.dev.ucenter.security.model.UserAuth;
import com.fast.dev.ucenter.security.util.TimeUtil;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.*;

/**
 * 用户令牌配置
 */
@Log
public class UserTokenCache {


    private final static String CacheName = "UserToken";


    @Autowired
    private UserTokenCache me;


    //缓存
    private Cache cache;


    /**
     * 重置缓存
     */
    public void cleanAllCache() {
        if (this.cache != null) {
            this.cache.asMap().clear();
            log.info("Rest All UserToken Cache");
        }
    }


    /**
     * 用户缓存 ，用户中心的id
     */
    public void cleanUserCache(String... uid) {
        if (uid == null || uid.length == 0) {
            return;
        }
        Map<String, UserAuth> cacheMap = this.cache.asMap();
        if (cacheMap.size() == 0) {
            return;
        }


        //开始执行清空缓存操作
        try {
            //通过遍历内存获取所有的
            Set<String> cleanUToken = new HashSet<>();
            for (Map.Entry<String, UserAuth> it : cacheMap.entrySet()) {
                Object value = it.getValue();
                if (value == null || !(value instanceof UserAuth)) {
                    continue;
                }
                UserAuth userAuth = it.getValue();
                if (userAuth != null && userAuth instanceof UserAuth) {
                    if (Arrays.binarySearch(uid, userAuth.getUid()) > -1) {
                        cleanUToken.add(it.getKey());
                    }
                }
            }


            //删除缓存
            cleanUToken.forEach((it) -> {
                cacheMap.remove(it);
            });
            log.info("Clean UserToken Cache : " + cleanUToken);
        } catch (Exception e) {
            e.printStackTrace();

            //如果出现异常则清空所有的缓存
            cleanAllCache();
        }


    }


    @Autowired
    private void init(CacheManager cacheManager) {
        this.cache = (Cache) cacheManager.getCache(CacheName).getNativeCache();
    }


    /**
     * 取出缓存
     */
    public UserAuth get(String uToken) {
        UserAuth userAuth = this.me.readCache(uToken);
        if (userAuth == null) {
            return null;
        }
        //缓存超时
        if (TimeUtil.getTime() > userAuth.getCreateTime() + userAuth.getExpireTime()) {
            this.me.remove(uToken);
            return null;
        }
        return userAuth;
    }


    /**
     * 设置缓存
     *
     * @param uToken
     * @param userAuthenticationModel
     * @return
     */
    @CachePut(value = CacheName, key = "#uToken")
    public UserAuth put(String uToken, UserAuth userAuthenticationModel) {
        log.info("Put Cache : " + uToken);
        return userAuthenticationModel;
    }

    /**
     * 取出缓存
     */
    @Cacheable(value = CacheName, key = "#uToken")
    public UserAuth readCache(String uToken) {
        return null;
    }

    /**
     * 删除缓存
     */
    @CacheEvict(value = CacheName, key = "#uToken")
    public void remove(String uToken) {
        log.info("remove Cache : " + uToken);
    }
}
