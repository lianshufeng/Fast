package com.fast.dev.ucenter.core.util;

import java.util.Random;
import java.util.UUID;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 */
public class RandomUtil {


    /**
     * 创建UUID
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    /**
     * 取指定大小的随机值
     * @param min
     * @param max
     * @return
     */
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }


}
