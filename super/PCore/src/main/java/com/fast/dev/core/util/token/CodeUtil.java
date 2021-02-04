package com.fast.dev.core.util.token;

/**
 * 编码生产工具
 */
public class CodeUtil {

    /**
     * 默认生成的长度
     */
    private final static int DefaultLength = 8;


    private final static char[] digits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F', 'G', 'H',
            'J', 'K', 'L', 'M', 'N',
            'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };


    /**
     * 转换编码
     *
     * @param code
     * @return
     */
    public static String get(long code) {
        return get(code, DefaultLength);
    }


    /**
     * 转换编码
     *
     * @param code
     * @return
     */
    public static String get(long code, final int length) {
        int radix = digits.length;
        long i = code;
        char[] buf = new char[65];
        int charPos = 64;
        boolean negative = (i < 0);
        if (!negative) {
            i = -i;
        }
        while (i <= -radix) {
            buf[charPos--] = digits[(int) (-(i % radix))];
            i = i / radix;
        }
        buf[charPos] = digits[(int) (-i)];

        if (negative) {
            buf[--charPos] = '-';
        }

        return full(new String(buf, charPos, (65 - charPos)), length);
    }


    /**
     * 自动填充保证默认的最低位
     *
     * @param info
     * @return
     */
    private static String full(String info, final int length) {
        StringBuffer sb = new StringBuffer(info);
        while (sb.length() < length) {
            sb.insert(0, digits[0]);
        }
        return sb.toString();
    }

}
