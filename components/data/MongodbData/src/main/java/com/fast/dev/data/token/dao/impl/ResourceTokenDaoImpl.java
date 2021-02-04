package com.fast.dev.data.token.dao.impl;

import com.fast.dev.data.token.dao.ResourceTokenDao;
import com.fast.dev.data.token.dao.extend.ResourceTokenDaoExtend;
import com.fast.dev.data.token.domain.ResourceToken;
import com.fast.dev.data.mongo.helper.DBHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;

@Slf4j
public class ResourceTokenDaoImpl implements ResourceTokenDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private ResourceTokenDao resourceTokenDao;


    @Override
    public long counter(String resourceName) {


        Query query = Query.query(Criteria.where("resourceName").is(resourceName).and("type").is(ResourceToken.ResourceType.System));

        //确保新增
        Update update = new Update();
        update.inc("counter", 1);
        update.setOnInsert("resourceName", resourceName);
        update.setOnInsert("type", ResourceToken.ResourceType.System);
        update.setOnInsert("uniqueIndex", String.format("%s_%s", ResourceToken.ResourceType.System, resourceName));
        this.dbHelper.updateTime(update);

        ResourceToken resourceToken = null;
        try {
            resourceToken = this.mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true).upsert(true), ResourceToken.class);
        } catch (Exception e) {
            //如果重复则重新取一次值
            if (e.getClass() == DuplicateKeyException.class) {
                return counter(resourceName);
            }
            e.printStackTrace();
            log.error("Exception : {}", e.getMessage());
        }
        return resourceToken.getCounter();
    }

    @Override
    public synchronized void record(String resourceName, long counter, long ttl) {
        ResourceToken resourceToken = new ResourceToken();
        resourceToken.setResourceName(resourceName);
        resourceToken.setCounter(counter);
        resourceToken.setTTL(new Date(ttl));
        resourceToken.setType(ResourceToken.ResourceType.User);


        this.dbHelper.saveTime(resourceToken);
        this.mongoTemplate.insert(resourceToken);
    }

    @Override
    public void updateTTL(long ttl, ResourceToken... resourceTokens) {

        if (resourceTokens == null || resourceTokens.length == 0) {
            return;
        }

        BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ResourceToken.class);
        for (ResourceToken resourceToken : resourceTokens) {
            Query query = Query.query(Criteria.where("resourceName").is(resourceToken.getResourceName()).and("counter").is(resourceToken.getCounter()).and("type").is(ResourceToken.ResourceType.User));

            Update update = new Update();
            update.set("TTL", new Date(ttl));
            this.dbHelper.updateTime(update);
            bulkOperations.updateOne(query, update);
        }

        bulkOperations.execute();

    }


}
