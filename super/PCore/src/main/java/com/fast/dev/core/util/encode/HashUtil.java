package com.fast.dev.core.util.encode;

import org.springframework.util.DigestUtils;

public class HashUtil {

    /**
     * 多文本hash工具
     *
     * @param texts
     * @return
     */
    public static String hash(String... texts) {
        StringBuilder sb = new StringBuilder();
        for (String text : texts) {
            sb.append(text + "_");
        }
        return DigestUtils.md5DigestAsHex(sb.toString().getBytes());
    }


}
