package com.fast.dev.pay.server.core.hb.model;

import com.fast.dev.pay.server.core.hb.type.ContractState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseAutoChargeContractModel {

    //企业id
    private String epId;

    //订单id
    private String id;

    //消费者手机号码
    private String consumePhone;

    //扣款的订单状态
    private ContractState contractState;


    //订单的冻结状态
    private ContractFreezeModel contractFreeze;


    //最近的一次扣款信息
    private ChargeContractModel lastChargeContract;


    //用户账户信息
    private HuaXiaUserAccountModel userAccount;

    //扣款信息
    private SuperAutoChargeModel autoCharge;

    //用户的开户信息
    private HuaXiaEnterpriseUserOpenAccountInfoModel userOpenAccountInfo;

    //订单名称
    private String orderName;

}
