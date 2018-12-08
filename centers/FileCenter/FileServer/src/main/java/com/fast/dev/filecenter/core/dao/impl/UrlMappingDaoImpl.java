package com.fast.dev.filecenter.core.dao.impl;

import com.fast.dev.filecenter.core.dao.extend.UrlMappingDaoExtend;
import com.fast.dev.filecenter.core.domain.UrlMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class UrlMappingDaoImpl implements UrlMappingDaoExtend {
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void updateUrlMapping(String userId, String fileId, String url) {
        Query query = new Query();
        Criteria criteria = Criteria.where("userId").is(userId).and("url").is(url);
        query.addCriteria(criteria);
        Update update = new Update();
        update.set("fileId",fileId);
        update.set("updateTime",System.currentTimeMillis());
        mongoOperations.updateFirst(query,update, UrlMapping.class);
    }
}
