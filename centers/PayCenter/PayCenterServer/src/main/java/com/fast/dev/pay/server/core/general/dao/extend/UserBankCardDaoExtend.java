package com.fast.dev.pay.server.core.general.dao.extend;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.pay.client.model.userbank.UserBankBindCardModel;

public interface UserBankCardDaoExtend {


    /**
     * 获取实体
     *
     * @return
     */
    Class<? extends SuperEntity> getEntity();


    /**
     * 插入数据
     *
     * @param userBankCard
     * @return
     */
    String insert(UserBankBindCardModel userBankCard);


    /**
     * 是否已存在绑定的卡
     *
     * @return
     */
    boolean existsCard(String uid, String payAccountId, String accountNumber);
}
