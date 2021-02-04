package com.fast.dev.pay.client.support.weixin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeiXinAmount {

    // 订单金额,单位分
    private Integer total;

    //用户支付金额
    private Integer payer_total;

    // 货币类型
    private String currency = "CNY";

    //用户支付币种
    private String payer_currency;


    public WeiXinAmount(int total) {
        this.total = total;
    }
}
