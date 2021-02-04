package com.fast.dev.filecenter.core.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.filecenter.core.dao.extend.UrlMappingDaoExtend;
import com.fast.dev.filecenter.core.domain.UrlMapping;

public interface UrlMappingDao extends MongoDao<UrlMapping>, UrlMappingDaoExtend {

    boolean existsByUserIdAndUrl(String userId, String url);

    UrlMapping findByUserIdAndUrl(String userId, String url);
}
