package com.fast.dev.pay.server.core.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.dao.exnted.IncTableDaoExtend;
import com.fast.dev.pay.server.core.domain.IncTable;

public interface IncTableDao extends MongoDao<IncTable>, IncTableDaoExtend {
}
