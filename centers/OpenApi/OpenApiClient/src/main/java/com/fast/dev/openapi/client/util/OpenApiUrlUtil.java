package com.fast.dev.openapi.client.util;

public class OpenApiUrlUtil {


    /**
     * 获取第一个
     *
     * @param url
     * @return
     */
    public static String getFirst(String url) {
        int at = url.indexOf("/", 1);
        if (at == -1) {
            return url;
        }
        return url.substring(0, at);
    }


    /**
     * 获取URI
     *
     * @param url
     * @return
     */
    public static String getAfterText(String url) {
        return url.substring(url.indexOf("/", 1), url.length());
    }

}
