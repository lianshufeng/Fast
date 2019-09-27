package com.fast.dev.ucenter.security.cache;

import com.fast.dev.ucenter.security.conf.UserTokenCacheConf;
import com.fast.dev.ucenter.security.model.UserAuth;
import com.fast.dev.ucenter.security.util.TimeUtil;
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

    @Autowired
    private void initCache() {
        tokenCache = new Cache("UserTokenCollection", userTokenConf.getMaxMemoryCount(), userTokenConf.isOverflowToDisk(),
                false, userTokenConf.getTimeToLiveSeconds(), userTokenConf.getTimeToIdleSeconds());
        cacheManager.addCache(tokenCache);
    }


    /**
     * 设置缓存
     */
    public void put(String uToken, UserAuth userAuthenticationModel) {
        Element element = new Element(uToken, userAuthenticationModel);
        this.tokenCache.put(element);
    }

    /**
     * 取出缓存
     */
    public UserAuth get(String uToken) {
        Element element = this.tokenCache.get(uToken);
        if (element == null) {
            return null;
        }
        UserAuth userAuth = (UserAuth) element.getObjectValue();
        //缓存超时
        if (TimeUtil.getTime() > userAuth.getCreateTime() + userAuth.getExpireTime() ) {
            this.remove(uToken);
            return null;
        }
        return userAuth;
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
