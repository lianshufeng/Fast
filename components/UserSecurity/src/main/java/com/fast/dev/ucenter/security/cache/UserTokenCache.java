package com.fast.dev.ucenter.security.cache;

import com.fast.dev.ucenter.security.conf.UserTokenCacheConf;
import com.fast.dev.ucenter.security.model.UserAuth;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 用户令牌配置
 */
public class UserTokenCache {

    @Autowired
    private UserTokenCacheConf userTokenConf;

    private CacheManager cacheManager = CacheManager.create();

    private Cache tokenCache = null;

    @PostConstruct
    private void initCache() {
        tokenCache = new Cache("UserTokenCollection", userTokenConf.getMaxMemoryCount(), userTokenConf.isOverflowToDisk(),
                false, userTokenConf.getTimeToLiveSeconds(), userTokenConf.getTimeToIdleSeconds());
        cacheManager.addCache(tokenCache);
    }


    /**
     * 设置缓存
     */
    public void put(String uToken, UserAuth userAuthenticationModel) {
        this.tokenCache.put(new Element(uToken, userAuthenticationModel));
    }

    /**
     * 取出缓存
     */
    public UserAuth get(String uToken) {
        Element element = this.tokenCache.get(uToken);
        if (element == null) {
            return null;
        }
        return (UserAuth) element.getObjectValue();
    }

    /**
     * 删除缓存
     */
    public void remove(String uToken) {
        this.tokenCache.remove(uToken);
    }


    @PreDestroy
    private void shutdown() {
        cacheManager.shutdown();
    }

}
