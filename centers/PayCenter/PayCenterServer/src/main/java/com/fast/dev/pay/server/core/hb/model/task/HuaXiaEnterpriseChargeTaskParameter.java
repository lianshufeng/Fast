package com.fast.dev.pay.server.core.hb.model.task;

import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseTaskParameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseChargeTaskParameter extends HuaXiaEnterpriseTaskParameter {

    //扣款时间,单位是天,如：第1天，第2天
    @Indexed
    private int time;

    //总金额
    @Indexed
    private long totalAmount;

    //支付金额
    @Indexed
    private long paymentAmount;

}
