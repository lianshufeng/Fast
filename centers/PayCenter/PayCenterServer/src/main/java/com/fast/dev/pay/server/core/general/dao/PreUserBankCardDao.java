package com.fast.dev.pay.server.core.general.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.general.dao.extend.PreUserBankCardDaoExtend;
import com.fast.dev.pay.server.core.general.domain.PreUserBankCard;

/**
 * 预绑定的用户银行卡
 */
public interface PreUserBankCardDao extends MongoDao<PreUserBankCard>, PreUserBankCardDaoExtend {


}
