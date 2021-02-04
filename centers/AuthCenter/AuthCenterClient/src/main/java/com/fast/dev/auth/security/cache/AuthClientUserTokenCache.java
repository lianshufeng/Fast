package com.fast.dev.auth.security.cache;

import com.fast.dev.auth.security.model.UserAutTokenCacheItem;
import com.fast.dev.ucenter.security.util.TimeUtil;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log
@Component
public class AuthClientUserTokenCache {

    private final static String CacheName = "UserToken";

    @Autowired
    private AuthClientUserTokenCache me;


    //缓存
    private Cache cache;


    @Autowired
    private void init(CacheManager cacheManager) {
        this.cache = (Cache) cacheManager.getCache(CacheName).getNativeCache();
        this.cache.cleanUp();
    }

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

        Map<String, Set<UserAutTokenCacheItem>> items = findUserByUid(uid);
        if (items == null) {
            return;
        }

        Set<String> uTokens = new HashSet<>();
        items.values().forEach((it) -> {
            uTokens.addAll(it.stream().map((item) -> {
                return item.getuToken();
            }).collect(Collectors.toSet()));
        });

        //删除缓存
        uTokens.forEach((it) -> {
            me.remove(it);
        });

    }


    /**
     * 通过用户id查询到缓存里的对象
     *
     * @param uid
     */
    public Map<String, Set<UserAutTokenCacheItem>> findUserByUid(String... uid) {
        if (uid == null || uid.length == 0) {
            return null;
        }
        Map<String, Set<UserAutTokenCacheItem>> ret = new HashMap<>();

        //缓存map
        Map<String, UserAutTokenCacheItem> cacheMap = this.cache.asMap();

        //反查用户id
        for (String u : uid) {
            Set<UserAutTokenCacheItem> item = ret.get(u);
            if (item == null) {
                item = new HashSet<>();
                ret.put(u, item);
            }
            for (Object val : cacheMap.values()) {
                if (val != null && val instanceof UserAutTokenCacheItem) {
                    UserAutTokenCacheItem it = (UserAutTokenCacheItem) val;
                    if (u.equals(it.getUid())) {
                        item.add(it);
                    }
                }
            }
        }

        return ret;
    }


    /**
     * 取出缓存
     */
    public UserAutTokenCacheItem get(String uToken) {
        if (!StringUtils.hasText(uToken)) {
            return null;
        }
        UserAutTokenCacheItem item = this.me.readCache(uToken);
        if (item == null) {
            return null;
        }
        //缓存超时
        if (TimeUtil.getTime() > item.getCacheTime() + item.getExpireTime()) {
            this.me.remove(uToken);
            return null;
        }
        return item;
    }


    /**
     * 设置缓存
     *
     * @return
     */
    @CachePut(value = CacheName, key = "#uToken")
    public UserAutTokenCacheItem put(String uToken, UserAutTokenCacheItem t) {
        log.info("Put Cache : " + t);
        return t;
    }

    /**
     * 取出缓存
     */
    @Cacheable(value = CacheName, key = "#uToken")
    public UserAutTokenCacheItem readCache(String uToken) {
        log.info("Miss Cache : " + uToken);
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
