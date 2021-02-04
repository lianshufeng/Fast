package com.fast.dev.ucenter.core.util;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 */
public class ValidateCodeUtil {

    /**
     * 创建仅数字的编码
     *
     * @param size
     * @return
     */
    public static String createOnlyNumber(int size) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < size; i++) {
            int ascCode = 48 + RandomUtil.randInt(0, 9);
            char value = (char) ascCode;
            stringBuffer.append(value);
        }
        return stringBuffer.toString();
    }

    /**
     * 创建随机长度的编码
     *
     * @param size
     * @return
     */
    public static String create(int size) {
        return RandomUtil.uuid(size);
    }
}
