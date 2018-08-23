package com.fast.dev.ucenter.core.util;

import java.util.Random;
import java.util.UUID;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 */
public class RandomUtil {


    /**
     * 取指定大小的随机值
     *
     * @param min
     * @param max
     * @return
     */
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }


    /**
     * 创建随机长度的编码
     *
     * @param size
     * @return
     */
    public static String uuid(int size) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(uuid());
        //保证长度一定够
        while (stringBuffer.length() < size) {
            stringBuffer.append(uuid());
        }
        return stringBuffer.substring(0, size);
    }


    /**
     * uuid
     *
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
