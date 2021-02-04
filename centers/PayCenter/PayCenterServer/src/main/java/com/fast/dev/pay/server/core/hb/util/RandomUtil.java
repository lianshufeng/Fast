package com.fast.dev.pay.server.core.hb.util;

import java.util.Random;

/**
 * 随机数工具类
 */
public class RandomUtil {


    public static int getRandom(int min, int max) {
        return new Random().nextInt(max) % (max - min + 1) + min;
    }


}
