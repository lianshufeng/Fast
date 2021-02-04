package com.fast.dev.pay.server.core.hb.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.pay.server.core.hb.model.*;
import com.fast.dev.pay.server.core.hb.type.ContractState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 华夏用户代扣订单
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseAutoChargeContract extends SuperEntity {

    @Indexed
    @DBRef(lazy = true)
    private HuaXiaEnterpriseAccount huaXiaEnterpriseAccount;


    //消费者手机号码
    @Indexed
    private String consumePhone;

    //扣款的订单状态
    @Indexed
    private ContractState contractState;


    //订单的冻结状态
    @Indexed
    private ContractFreezeModel contractFreeze;


    //最近的一次扣款信息
    @Indexed
    private ChargeContractModel lastChargeContract;


    //用户账户信息
    @Indexed
    private HuaXiaUserAccountModel userAccount;

    //扣款信息
    @Indexed
    private SuperAutoChargeModel autoCharge;


    //用户的开户在华夏的开户信息
    @Indexed
    private HuaXiaEnterpriseUserOpenAccountInfoModel userOpenAccountInfo;


    //订单名称
    @Indexed
    private String orderName;


    @Indexed
    @DBRef(lazy = true)
    private HuaXiaEnterpriseAutoChargeTemplate template;


}
