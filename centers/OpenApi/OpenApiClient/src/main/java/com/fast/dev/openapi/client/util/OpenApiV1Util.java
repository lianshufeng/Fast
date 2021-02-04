package com.fast.dev.openapi.client.util;


import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.encrypt.AesUtil;
import lombok.SneakyThrows;

import java.util.Base64;

public class OpenApiV1Util {


    /**
     * 加密
     *
     * @param key
     * @param content
     * @return
     */
    public static String encrypt(final String key, Object content) {
        //序列化
        String json = JsonUtil.toJson(content);
        //加密
        byte[] buff = AesUtil.encrypt(json, key);
        //转到base64编码
        return Base64.getEncoder().encodeToString(buff);
    }


    /**
     * 解密
     *
     * @param key  密码，必须为8的整数倍
     * @param data base64编码
     * @return
     */
    @SneakyThrows
    public static <T> T decrypt(final String key, final String data, Class<T> cls) {
        //解密
        byte[] buff = AesUtil.decrypt(Base64.getDecoder().decode(data), key);
        //反序列化
        return JsonUtil.toObject(new String(buff, AesUtil.DefaultCharSet), cls);
    }


}
