package com.fast.dev.open.api.server.core.helper;

import com.fast.dev.auth.client.model.EnterpriseModelAndSK;
import com.fast.dev.auth.client.service.EnterpriseService;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnterpriseHelper {

    @Autowired
    private EnterpriseService enterpriseService;

    private final static String CacheName = "EnterpriseCache";

    //缓存
    private Cache<String, EnterpriseModelAndSK> cache;


    @Autowired
    private void init(CacheManager cacheManager) {
        this.cache = (Cache) cacheManager.getCache(CacheName).getNativeCache();
        this.cache.cleanUp();
    }


    /**
     * 通过AK 查询企业模型
     *
     * @param ak
     */
    public EnterpriseModelAndSK query(String ak) {
        EnterpriseModelAndSK o = this.cache.getIfPresent(ak);
        if (o != null) {
            return o;
        }
        EnterpriseModelAndSK ret = enterpriseService.getFromAK(ak);
        if (ret == null) {
            return null;
        }
        log.info("cache : {} -> {} ", ak, ret);
        this.cache.put(ak, ret);
        return ret;
    }


}
