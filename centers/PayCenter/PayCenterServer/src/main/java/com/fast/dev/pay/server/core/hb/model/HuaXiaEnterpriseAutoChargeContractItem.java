package com.fast.dev.pay.server.core.hb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseAutoChargeContractItem extends HuaXiaEnterpriseAutoChargeContractModel {

    //已扣款金额
    private long chargeAmount;

    //总金额
    private long totalAmount;

    //开始扣款时间
    private long startChargeTime;

    //结束扣款时间
    private long endChargeTime;

    //扣款状态
    private ContractRequestModel.ChargeState chargeState;

    //合同状态
    private ContractRequestModel.OrderState orderState;

    //冻结状态
    private ContractRequestModel.FreezeState freezeState;


}
