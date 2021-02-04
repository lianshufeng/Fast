package com.fast.dev.pay.server.core.hb.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaEnterpriseAutoChargeContractDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeContract;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeTemplate;

public interface HuaXiaEnterpriseAutoChargeContractDao extends MongoDao<HuaXiaEnterpriseAutoChargeContract>, HuaXiaEnterpriseAutoChargeContractDaoExtend {

    /**
     * 通过企业和id查询合同，同时为了验证合同是否属于该企业
     *
     * @param account
     * @param id
     * @return
     */
    HuaXiaEnterpriseAutoChargeContract findByHuaXiaEnterpriseAccountAndId(HuaXiaEnterpriseAccount account, String id);

    boolean existsByUserAccount_UserMobile(String phone);

    HuaXiaEnterpriseAutoChargeContract findTop1ByUserAccount_UserMobile(String phone);
}
