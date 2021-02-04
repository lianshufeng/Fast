package com.fast.dev.auth.center.server.core.dao.impl;

import com.fast.dev.auth.center.server.core.dao.extend.InternalResourceDaoExtend;
import com.fast.dev.auth.client.model.GeneralItemModel;
import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.mongodb.bulk.BulkWriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public abstract class

InternalResourceDaoImpl<T> implements InternalResourceDaoExtend<T> {
    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private DBHelper dbHelper;


    @Override
    public long put(Map<String, String> auth) {
        if (auth == null || auth.size() == 0) {
            return 0;
        }
        //批量查询并修改
        BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, entityClass());
        auth.entrySet().forEach((it) -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(it.getKey()));

            Update update = new Update();
            update.set("name", it.getKey());
            update.set("remark", it.getValue());
            this.dbHelper.saveTime(update);

            bulkOperations.upsert(query, update);
        });


        BulkWriteResult bulkWriteResult = bulkOperations.execute();

        return bulkWriteResult.getModifiedCount() + bulkWriteResult.getUpserts().size();
    }

    @Override
    public long remove(String... authName) {
        Query query = new Query(Criteria.where("name").in(Arrays.asList(authName)));
        return this.mongoTemplate.remove(query, entityClass()).getDeletedCount();
    }


    @Override
    public Page<T> findByNameLike(String name, Pageable pageable) {
        Query query = Query.query(Criteria.where("name").regex(name));
        return this.dbHelper.pages(query, pageable, entityClass());
    }


    @Override
    public Page<T> list(GeneralItemModel generalItemModel, Set<String> ignoreValue, Pageable pageable) {
        Criteria criteria = new Criteria();
        //备注的模糊匹配
        if (StringUtils.hasText(generalItemModel.getRemark())) {
            criteria = criteria.regex(generalItemModel.getRemark());
        }

        //名字模糊匹配
        if (StringUtils.hasText(generalItemModel.getName())) {
            criteria = criteria.andOperator(
                    Criteria.where("name").nin(ignoreValue),
                    Criteria.where("name").regex(generalItemModel.getName())
            );
        } else {
            criteria = criteria.and("name").nin(ignoreValue);
        }

        Query query = new Query(criteria);
        return this.dbHelper.pages(query, pageable, entityClass());
    }

    /**
     * 实体
     *
     * @return
     */
    public abstract Class<? extends SuperEntity> entityClass();


}
