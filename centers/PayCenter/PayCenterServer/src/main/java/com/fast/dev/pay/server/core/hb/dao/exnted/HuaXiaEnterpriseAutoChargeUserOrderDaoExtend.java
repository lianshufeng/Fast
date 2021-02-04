package com.fast.dev.pay.server.core.hb.dao.exnted;

import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeUserOrder;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAutoChargeUserOrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HuaXiaEnterpriseAutoChargeUserOrderDaoExtend {

    /**
     * 查询用户订单
     * @param model
     * @param pageable
     * @return
     */
    Page<HuaXiaEnterpriseAutoChargeUserOrder> list(HuaXiaEnterpriseAutoChargeUserOrderModel model, Pageable pageable);


    /**
     * 设置用户订单为使用过
     * @param id
     * @return
     */
    boolean setUserOrderUsed(String id);

}
