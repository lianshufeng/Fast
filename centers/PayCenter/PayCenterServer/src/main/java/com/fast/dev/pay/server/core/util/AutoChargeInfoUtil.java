package com.fast.dev.pay.server.core.util;

import com.fast.dev.pay.server.core.hb.model.AutoChargeInfo;

public class AutoChargeInfoUtil {

    /**
     * 统计总金额
     *
     * @param autoChargeInfos
     * @return
     */
    public static long countAmount(AutoChargeInfo[] autoChargeInfos) {
        long total = 0;
        for (AutoChargeInfo chargeInfo : autoChargeInfos) {
            total += chargeInfo.getAmount();
        }
        return total;
    }

}
