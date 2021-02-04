package com.fast.dev.mq.mqserver.core.util;

import java.util.UUID;

public class TokenUtil {

    /**
     * 创建token
     *
     * @return
     */
    public static String create() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
