package com.fast.dev.pay.client.util;

import com.fast.dev.core.util.token.TokenUtil;
import com.fast.dev.pay.client.type.AccountType;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单工具
 */
public class OrderUtil {

    private final static DateTimeFormatter OrderDF = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");


    /**
     * 订单生产工具
     */
    public static String build(AccountType accountType, String serviceCode) {
        return build(accountType,serviceCode,14);
    }



    /**
     * 订单生产工具
     */
    public static String build(AccountType accountType, String serviceCode,int fullSize) {
        Assert.isTrue(serviceCode.length() == 2, "业务编码必须为2位");
        String timeText = OrderDF.format(LocalDateTime.now());
        return accountType.getCode() + serviceCode + TokenUtil.create(fullSize) + timeText;
    }


}
