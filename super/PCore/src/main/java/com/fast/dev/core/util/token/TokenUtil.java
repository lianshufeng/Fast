package com.fast.dev.core.util.token;

import com.fast.dev.core.util.encode.HashUtil;

import java.util.UUID;

public class TokenUtil {

    /**
     * 创建令牌
     *
     * @return
     */
    public static String create() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 创建令牌并将UUID的值Hash
     *
     * @return
     */
    public static String createAndHash() {
        return HashUtil.hash(create());
    }


    /**
     * 创建令牌，长度
     *
     * @param size
     * @return
     */
    public static String create(int size) {
        String ret = createAndHash();
        ret = limitMaxText(ret, size);
        while (ret.length() < size) {
            ret += createAndHash();
        }
        return limitMaxText(ret, size);
    }


    /**
     * 限制最大文本
     *
     * @param text
     * @param maxCount
     * @return
     */
    private static String limitMaxText(String text, int maxCount) {
        return text.length() > maxCount ? text.substring(0, maxCount) : text;
    }


}
