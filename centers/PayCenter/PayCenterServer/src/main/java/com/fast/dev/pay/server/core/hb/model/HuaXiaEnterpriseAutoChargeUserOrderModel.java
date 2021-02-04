package com.fast.dev.pay.server.core.hb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuaXiaEnterpriseAutoChargeUserOrderModel extends SuperAutoChargeModel {

    //消费者手机号码
    private String consumePhone;

    //是否已使用
    private Boolean used;

    //用户订单模型
    private String name;

    //模板id
    private String id;

    //企业id
    private String epId;

    //华夏企业账户id
    private String hbId;

    //华夏企业账户编码
    private String hbCode;

    //到期时间
    private Long endTime;

    //企业工作时间
    private int epWorkTime;

    //企业名
    private String epName;

    //是否已过期
    private boolean timeOut;

    //客服电话
    private String customerPhone;

    private Integer page;

    private Integer size;


}
