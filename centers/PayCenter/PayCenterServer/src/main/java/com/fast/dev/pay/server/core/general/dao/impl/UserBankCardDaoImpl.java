package com.fast.dev.pay.server.core.general.dao.impl;

import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.client.model.userbank.UserBankBindCardModel;
import com.fast.dev.pay.server.core.general.dao.EnterpriseAccountDao;
import com.fast.dev.pay.server.core.general.dao.EnterprisePayAccountDao;
import com.fast.dev.pay.server.core.general.dao.extend.UserBankCardDaoExtend;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;
import com.fast.dev.pay.server.core.general.domain.UserBankCard;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

public class UserBankCardDaoImpl implements UserBankCardDaoExtend {

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected DBHelper dbHelper;


    @Override
    public Class<? extends SuperEntity> getEntity() {
        return UserBankCard.class;
    }


    /**
     * 构建用户银行卡模型
     *
     * @param model
     * @return
     */
    protected SuperEntity buildUserBankBindCardEntity(UserBankBindCardModel model) {
        UserBankCard userBankCard = (UserBankCard) BeanUtil.newClass(getEntity());
        BeanUtils.copyProperties(model, userBankCard, "id");
        dbHelper.saveTime(userBankCard);
        return userBankCard;
    }


    @Override
    public String insert(UserBankBindCardModel model) {
        UserBankCard userBankCard = (UserBankCard) buildUserBankBindCardEntity(model);
        this.mongoTemplate.save(userBankCard);
        return userBankCard.getId();
    }

    @Override
    public boolean existsCard(String uid, String payAccountId, String accountNumber) {
        return this.mongoTemplate.exists(new Query(
                Criteria.where("uid").is(uid)
                        .and("payAccountId").is(payAccountId)
                        .and("accountNumber").is(accountNumber)
        ), getEntity());
    }


}
