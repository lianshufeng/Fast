package com.fast.dev.mq.mqserver.core.util;

import java.util.Map;

public class FormUtil {


    /**
     * 转换到文本
     *
     * @param parameter
     * @return
     */
    public static String toText(Map<String, Object> parameter) {
        StringBuilder sb = new StringBuilder();
        parameter.entrySet().forEach((it) -> {
            sb.append(it.getKey() + "=" + it.getValue() + "&");
        });
        return sb.toString();
    }

}
