package com.fast.dev.pay.server.core.cpcn.param;

import com.fast.dev.pay.server.core.general.domain.UserBankCard;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayModel {

    /**
     * 分账结果
     */
    private String splitResult;

    /**
     * 用户卡信息
     */
    private UserBankCard userBankCard;

    /**
     * 用户绑定流水号
     */
    private String txSNBinding;
}


