package com.fast.dev.pay.server.core.general.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.general.dao.extend.CallBackOrderDaoExtend;
import com.fast.dev.pay.server.core.general.domain.CallBackOrder;

public interface CallBackOrderDao extends MongoDao<CallBackOrder>, CallBackOrderDaoExtend {
}
