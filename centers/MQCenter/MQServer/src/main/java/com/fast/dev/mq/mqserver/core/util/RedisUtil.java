package com.fast.dev.mq.mqserver.core.util;

public class RedisUtil {

    /**
     * 创建key
     *
     * @param serviceName
     * @param value
     * @return
     */
    public static String buildKey(String serviceName, String value) {
        return serviceName + "_" + value;
    }


}
