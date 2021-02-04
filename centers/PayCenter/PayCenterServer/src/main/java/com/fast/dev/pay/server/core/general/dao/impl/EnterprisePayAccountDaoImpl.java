package com.fast.dev.pay.server.core.general.dao.impl;

import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.client.model.EnterprisePayAccountModel;
import com.fast.dev.pay.server.core.general.dao.extend.EnterprisePayAccountDaoExtend;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashSet;
import java.util.Set;

public class EnterprisePayAccountDaoImpl implements EnterprisePayAccountDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;


    @Override
    public String update(EnterprisePayAccountModel enterprisePayAccountModel) {

        //企业支付账号
        EnterprisePayAccount enterprisePayAccount = this.mongoTemplate.findOne(Query.query(Criteria.where("_id").is(enterprisePayAccountModel.getId())), EnterprisePayAccount.class);
        if (enterprisePayAccount == null) {
            enterprisePayAccount = new EnterprisePayAccount();
            enterprisePayAccount.setEnterpriseId(enterprisePayAccountModel.getEnterpriseId());
            this.dbHelper.saveTime(enterprisePayAccount);
        }


        //拷贝非空的字段
        Set<String> ignore = new HashSet<>(Set.of("id"));
        BeanUtil.getNullPropertyNames(enterprisePayAccountModel, ignore);
        BeanUtils.copyProperties(enterprisePayAccountModel, enterprisePayAccount, ignore.toArray(new String[0]));

        this.dbHelper.updateTime(enterprisePayAccount);
        this.mongoTemplate.save(enterprisePayAccount);

        return enterprisePayAccount.getId();
    }


}
