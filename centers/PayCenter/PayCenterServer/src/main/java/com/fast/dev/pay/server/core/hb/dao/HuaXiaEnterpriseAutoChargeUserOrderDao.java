package com.fast.dev.pay.server.core.hb.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaEnterpriseAutoChargeUserOrderDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeUserOrder;

public interface HuaXiaEnterpriseAutoChargeUserOrderDao extends MongoDao<HuaXiaEnterpriseAutoChargeUserOrder>, HuaXiaEnterpriseAutoChargeUserOrderDaoExtend {

    /**
     * 通过用户订单id查询到订单实体
     *
     * @param id
     * @return
     */
    HuaXiaEnterpriseAutoChargeUserOrder findTop1ById(String id);
}
