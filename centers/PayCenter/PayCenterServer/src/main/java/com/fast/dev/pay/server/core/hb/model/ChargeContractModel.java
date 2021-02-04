package com.fast.dev.pay.server.core.hb.model;

import com.fast.dev.pay.server.core.hb.type.ChargeState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 扣款模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargeContractModel {

    //扣款时间
    private long time;

    //扣款状态
    private ChargeState state;

}
