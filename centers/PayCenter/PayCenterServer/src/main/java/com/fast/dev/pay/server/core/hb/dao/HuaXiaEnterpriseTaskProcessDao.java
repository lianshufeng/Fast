package com.fast.dev.pay.server.core.hb.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaEnterpriseTaskProcessDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTaskProcess;

public interface HuaXiaEnterpriseTaskProcessDao extends MongoDao<HuaXiaEnterpriseTaskProcess>, HuaXiaEnterpriseTaskProcessDaoExtend {
}
