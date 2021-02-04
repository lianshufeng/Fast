package com.fast.dev.pay.server.core.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.token.domain.ResourceToken;
import com.fast.dev.pay.server.core.dao.exnted.IncTableDaoExtend;
import com.fast.dev.pay.server.core.domain.IncTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;

@Slf4j
public class IncTableDaoImpl implements IncTableDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;


    @Override
    public long inc(String serviceName) {
        return inc(serviceName, null);
    }

    @Override
    public long inc(String serviceName, Long timeOut) {

        Query query = new Query(Criteria.where("serviceName").is(serviceName));

        Update update = new Update();
        if (timeOut != null) {
            update.set("TTL", new Date(this.dbHelper.getTime() + timeOut));
        }

        update.inc("count");
        update.setOnInsert("serviceName", serviceName);


        this.dbHelper.saveTime(update);

        IncTable incTable = null;
        try {
            incTable = this.mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true), IncTable.class);
        } catch (Exception e) {
            //如果重复则重新取一次值
            if (e.getClass() == DuplicateKeyException.class) {
                return inc(serviceName, timeOut);
            }
            e.printStackTrace();
            log.error("Exception : {}", e.getMessage());
        }

        return incTable.getCount();
    }

    @Override
    public boolean reset(String serviceName) {

        Query query = new Query(Criteria.where("serviceName").is(serviceName));

        Update update = new Update();
        update.set("count", 0);
        update.setOnInsert("serviceName", serviceName);

        return this.mongoTemplate.upsert(query, update, IncTable.class).getModifiedCount() > 0;
    }
}
