package com.fast.dev.ucenter.core.helper;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 */
public class ValidataCodeUtil {

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
            ;
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
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(RandomUtil.uuid());
        //保证长度一定够
        while (stringBuffer.length() < size) {
            stringBuffer.append(RandomUtil.uuid());
        }
        return stringBuffer.substring(0, size);
    }
}
