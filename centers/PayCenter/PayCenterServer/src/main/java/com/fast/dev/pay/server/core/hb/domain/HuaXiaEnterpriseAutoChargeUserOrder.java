package com.fast.dev.pay.server.core.hb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 华夏企业预代扣订单
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseAutoChargeUserOrder extends SuperAutoCharge {

    //华夏的企业账户
    @Indexed
    @DBRef(lazy = true)
    private HuaXiaEnterpriseAccount huaXiaEnterpriseAccount;


    //用户订单名称
    @Indexed
    private String name;


    //到期时间
    @Indexed
    private Long endTime;


    //消费者手机号码
    @Indexed
    private String consumePhone;


    //是否已使用
    @Indexed
    private boolean used;


}
