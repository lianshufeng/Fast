package com.fast.dev.ucenter.core.helper;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 */
public class TokenUtil {


    /**
     * 创建令牌
     *
     * @return
     */
    public static String create() {
        return RandomUtil.uuid();
    }

}
