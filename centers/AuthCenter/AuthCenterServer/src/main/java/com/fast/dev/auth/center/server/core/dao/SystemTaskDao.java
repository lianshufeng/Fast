package com.fast.dev.auth.center.server.core.dao;

import com.fast.dev.auth.center.server.core.dao.extend.SystemTaskDaoExtend;
import com.fast.dev.auth.center.server.core.domain.SystemTask;
import com.fast.dev.data.mongo.dao.MongoDao;

/**
 * 系统任务表
 */
public interface SystemTaskDao extends MongoDao<SystemTask>, SystemTaskDaoExtend {
}
