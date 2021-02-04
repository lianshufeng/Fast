package com.fast.dev.filecenter.core.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.filecenter.core.dao.extend.UrlCacheDaoExtend;
import com.fast.dev.filecenter.core.domain.UrlCache;

public interface UrlCacheDao extends MongoDao<UrlCache>, UrlCacheDaoExtend {
    UrlCache findByUserIdAndUrl(String userId,String url);

    void deleteByUserIdAndUrlMappingId(String userId,String urlMappingId);
}
