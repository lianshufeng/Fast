package com.fast.dev.pay.server.core.hb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 企业(代扣)套餐模板
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseAutoChargeTemplate extends SuperAutoCharge {

    @Indexed
    @DBRef(lazy = true)
    private HuaXiaEnterpriseAccount huaXiaEnterpriseAccount;

    //模板编码
    @Indexed
    private String code;


    //模板名称(套餐名称)
    @Indexed
    private String name;

    //套餐开始时间
    @Indexed
    private Long startTime;

    //套餐结束时间
    @Indexed
    private Long endTime;

    //是否停用
    @Indexed
    private boolean disable;


}
