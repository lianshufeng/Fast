package com.fast.dev.pay.server.core.general.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.server.core.dao.IncTableDao;
import com.fast.dev.pay.server.core.general.dao.extend.EnterpriseAccountDaoExtend;
import com.fast.dev.pay.server.core.general.domain.EnterpriseAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class EnterpriseAccountDaoImpl implements EnterpriseAccountDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private IncTableDao incTableDao;


    @Override
    public EnterpriseAccount findAndSave(String enterpriseId) {

        //计算当前的总企业数
        long count = this.mongoTemplate.count(new Query(), EnterpriseAccount.class);

        //查询企业
        Query query = new Query();
        query.addCriteria(Criteria.where("enterpriseId").is(enterpriseId));

        long time = this.dbHelper.getTime();

        Update update = new Update();
        update.set("enterpriseId", enterpriseId);
        update.setOnInsert("createTime", time);
        this.dbHelper.updateTime(update);
        EnterpriseAccount enterpriseAccount = this.mongoTemplate.findAndModify(query, update, EnterpriseAccount.class);


        return enterpriseAccount;
    }

}
