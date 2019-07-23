package com.fast.dev.promise.server.core.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.promise.server.core.dao.extend.ErrorTryTableDaoExtend;
import com.fast.dev.promise.server.core.domain.ErrorTryTable;

public interface ErrorTryTableDao extends MongoDao<ErrorTryTable>, ErrorTryTableDaoExtend {


}
