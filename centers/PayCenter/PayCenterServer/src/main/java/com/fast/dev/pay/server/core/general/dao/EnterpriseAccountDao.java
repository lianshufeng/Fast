package com.fast.dev.pay.server.core.general.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.general.dao.extend.EnterpriseAccountDaoExtend;
import com.fast.dev.pay.server.core.general.domain.EnterpriseAccount;

/**
 * 企业账户Dao
 */
public interface EnterpriseAccountDao extends MongoDao<EnterpriseAccount>, EnterpriseAccountDaoExtend {

}
