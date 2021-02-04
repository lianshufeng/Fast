package com.fast.dev.robot.robotserver.core.util;

import java.util.Random;
import java.util.UUID;

/**
 * 随机数生成器
 */
public class RandomCodeUtil {


    //随机数
    private final static char[] NumberSet = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };


    private final static char[] CharSet = {
            '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F', 'G', 'H',
            'J', 'K', 'L', 'M', 'N',
            'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private final static char[] TokenSet = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };


    /**
     * 获取编码
     *
     * @return
     */
    public static String getCode(boolean isNumber, int size) {
        return getCode(isNumber ? NumberSet : CharSet, size);
    }


    /**
     * 创建令牌
     *
     * @return
     */
    public static String nextToken(int size) {
        return UUID.randomUUID().toString().replaceAll("-","");
    }


    /**
     * 获取编码
     *
     * @param charSet
     * @param size
     * @return
     */
    private static String getCode(char[] charSet, int size) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            int index = getRandom(0, charSet.length - 1);
            sb.append(charSet[index]);
        }
        return sb.toString();
    }

    /**
     * 取指定返回的随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }


}
