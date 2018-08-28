package com.fast.dev.ucenter.core.util;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 * 密码工具
 */
public class PassWordUtil {


    /**
     * 校验
     *
     * @param salt
     * @param source
     * @param target
     * @return
     */
    public static boolean validate(String salt, String source, String target) {
        if (StringUtils.isEmpty(salt) || StringUtils.isEmpty(source) || StringUtils.isEmpty(target)) {
            return false;
        }
        return enCode(salt, source).equals(target);
    }


    /**
     * 编码
     *
     * @param salt
     * @param source
     * @return
     */
    public static String enCode(String salt, String source) {
        String pd = DigestUtils.md5DigestAsHex(source.getBytes());
        return DigestUtils.md5DigestAsHex((salt + "_" + pd + "_" + salt).getBytes());
    }


}
