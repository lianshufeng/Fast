package com.fast.dev.data.mongo.helper;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 批量查询
 */
@Component
public class BatchQueryHelper {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 批量查询
     *
     * @param entityCls
     * @param items
     * @param <T>
     * @return
     */
    public <T extends SuperEntity> List<T> batchQuery(Class<T> entityCls, Collection<Map<String, Object>> items) {
        return this.mongoTemplate.find(Query.query(EntityObjectUtil.createQueryBatch(items)), entityCls);
    }


}
