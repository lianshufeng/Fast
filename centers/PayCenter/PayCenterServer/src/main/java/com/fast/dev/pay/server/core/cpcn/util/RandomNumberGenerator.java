package com.fast.dev.pay.server.core.cpcn.util;

import java.util.Random;

public class RandomNumberGenerator {

    private static Random random = new Random();
    private static final char[] CHARACTER_TABLE = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public RandomNumberGenerator() {
    }

    public static String getRandomNumber(int length) {
        String string;
        for(string = String.valueOf(Math.abs(random.nextLong())); string.length() < length; string = string + String.valueOf(Math.abs(random.nextLong()))) {
        }

        return string.substring(0, length);
    }

    public static String getRandomCharAndNumber(int length, boolean upperCaseSupported) {
        StringBuffer rsb = new StringBuffer();
        int i;
        if (upperCaseSupported) {
            for(i = 0; i < length; ++i) {
                rsb.append(CHARACTER_TABLE[random.nextInt(62)]);
            }
        } else {
            for(i = 0; i < length; ++i) {
                rsb.append(CHARACTER_TABLE[random.nextInt(36)]);
            }
        }

        return rsb.toString();
    }
}
