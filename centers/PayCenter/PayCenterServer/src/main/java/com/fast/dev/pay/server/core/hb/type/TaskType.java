package com.fast.dev.pay.server.core.hb.type;

import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseTaskParameter;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseChargeTaskParameter;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseCheckChargeTaskParameter;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseCheckFinishTaskParameter;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseFreezeTaskParameter;
import lombok.Getter;

/**
 * 任务类型
 */
public enum TaskType {
    /**
     * 冻结
     */
    Freeze(HuaXiaEnterpriseFreezeTaskParameter.class),


    /**
     * 扣款
     */
    Charge(HuaXiaEnterpriseChargeTaskParameter.class),


    /**
     * 检查扣款是否完成
     */
    CheckCharge(HuaXiaEnterpriseCheckChargeTaskParameter.class),



    /**
     * 检查任务是否已完成
     */
    CheckFinish(HuaXiaEnterpriseCheckFinishTaskParameter.class),


    /**
     * 查询冻结金额
     */
    QueryFreezeAmount(null);


    @Getter
    private Class<? extends HuaXiaEnterpriseTaskParameter> parameterEntity;


    TaskType(Class<? extends HuaXiaEnterpriseTaskParameter> parameterEntity) {
        this.parameterEntity = parameterEntity;
    }
}
