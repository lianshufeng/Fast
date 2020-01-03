package com.fast.dev.user.auth.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.user.auth.dao.extend.UserIdentityUpdateListDaoExtend;
import com.fast.dev.user.auth.domain.UserIdentityUpdateList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserIdentityUpdateListDaoImpl implements UserIdentityUpdateListDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Override
    public void addUser(String... userIds) {
        this.addUser(new HashSet<>(Arrays.asList(userIds)));
    }

    @Override
    public Set<String> findAndeRemoveUpdateUser(int limit) {
        String uuid = UUID.randomUUID().toString();
        BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, UserIdentityUpdateList.class);
        for (int i = 0; i < limit; i++) {

            Query query = Query.query(Criteria.where("uuid").ne(uuid));

            Update update = new Update();
            update.set("uuid", uuid);
            this.dbHelper.saveTime(update);
            bulkOperations.updateOne(query, update);
        }
        bulkOperations.execute();

        return this.mongoTemplate.findAllAndRemove(Query.query(Criteria.where("uuid").is(uuid)), UserIdentityUpdateList.class).stream().map((it) -> {
            return it.getUserId();
        }).collect(Collectors.toSet());
    }

    @Override
    public void addUser(Set<String> userIds) {
        if (userIds == null || userIds.size() == 0) {
            return;
        }

        BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, UserIdentityUpdateList.class);
        userIds.forEach((it) -> {
            //查询是否存在，防止重复加入
            Query query = Query.query(Criteria.where("userId").is(it));
            Update update = new Update();
            update.setOnInsert("userId", it);
            this.dbHelper.saveTime(update);
            bulkOperations.upsert(query, update);
        });

        //批量执行
        bulkOperations.execute();
    }


}
