package com.fast.dev.pay.server.core.hb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseAutoChargeTemplateModel extends SuperAutoChargeModel {

    //模板id
    private String id;

    //企业id
    private String epId;

    //华夏企业账户id
    private String hbId;

    //华夏企业账户编码
    private String hbCode;

    //模板编码
    private String code;


    //模板名称(套餐名称)
    private String name;

    //套餐开始时间
    private Long startTime;

    //套餐结束时间
    private Long endTime;

    //是否停用
    private boolean disable;


    //企业工作时间
    private int epWorkTime;

    //企业名
    private String epName;

    //客服电话
    private String customerPhone;

}
