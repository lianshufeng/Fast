package com.fast.dev.pay.server.core.general.dao.impl;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.pay.client.model.userbank.UserBankBindCardModel;
import com.fast.dev.pay.server.core.general.conf.UserBindBankCardConf;
import com.fast.dev.pay.server.core.general.dao.extend.PreUserBankCardDaoExtend;
import com.fast.dev.pay.server.core.general.domain.PreUserBankCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;

public class PreUserBankCardDaoImpl extends UserBankCardDaoImpl implements PreUserBankCardDaoExtend {

    @Autowired
    private UserBindBankCardConf userBindBankCardConf;

    @Override
    public Class<? extends SuperEntity> getEntity() {
        return PreUserBankCard.class;
    }


    /**
     * 取绑定操作过期时间
     *
     * @return
     */
    private Date getTTL() {
        return new Date(this.dbHelper.getTime() + userBindBankCardConf.getBindCardTimeOutMinute() * 60 * 1000);
    }


    @Override
    public String insert(UserBankBindCardModel model) {
        PreUserBankCard userBankCard = (PreUserBankCard) buildUserBankBindCardEntity(model);
        //设置过期时间
        userBankCard.setTTL(getTTL());
        mongoTemplate.save(userBankCard);
        return userBankCard.getId();
    }

    @Override
    public PreUserBankCard findAndRemoveById(String preBindId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(preBindId));
        return this.mongoTemplate.findAndRemove(query, PreUserBankCard.class) ;
    }

    @Override
    public boolean delCard(String uid, String payAccountId, String accountNumber) {
        return this.mongoTemplate.remove(new Query(
                Criteria.where("uid").is(uid)
                        .and("payAccountId").is(payAccountId)
                        .and("accountNumber").is(accountNumber)
        ), getEntity()).getDeletedCount() > 0 ? true:false;
    }
}
