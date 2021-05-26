package com.fast.dev.pay.server.core.cpcn.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class GUID {

    private static final Random random = new Random();
    private static final SimpleDateFormat sdf_day = new SimpleDateFormat("yyMMdd");
    private static final SimpleDateFormat sdf_minute = new SimpleDateFormat("yyMMddHHmm");
    private static final SimpleDateFormat sdf_second = new SimpleDateFormat("yyMMddHHmmss");
    private static final SimpleDateFormat sdf_second2 = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat sdf_millisecond = new SimpleDateFormat("yyMMddHHmmssSSS");
    private static final SimpleDateFormat sdf_millisecond2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static final char[] CHARACTER_TABLE = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    public static final int LEN_16 = 16;
    public static final int LEN_19 = 19;
    public static final int LEN_20 = 20;
    public static final int LEN_25 = 25;
    public static final int LEN_27 = 27;

    public GUID() {
    }

    /** @deprecated */
    @Deprecated
    public static String generateGUID() {
        long time = System.currentTimeMillis();
        long randomNumber = random.nextLong();
        StringBuffer sb = new StringBuffer("www.cpcn.com.cn");
        sb.append(time).append(randomNumber);
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
        }

        messageDigest.update(sb.toString().getBytes());
        return StringUtil.bytes2hex(messageDigest.digest());
    }

    public static String genTxNo(int length) throws Exception {
        String txNo = null;
        switch(length) {
            case 16:
                txNo = genTxNo(16, false);
                break;
            case 17:
            case 18:
            case 21:
            case 22:
            case 23:
            case 24:
            case 26:
            default:
                throw new Exception("don't support length[" + length + "]");
            case 19:
                txNo = genTxNo(19, false);
                break;
            case 20:
                txNo = genTxNo(20, false);
                break;
            case 25:
                txNo = genTxNo(25, false);
                break;
            case 27:
                txNo = genTxNo(27, false);
        }

        return txNo;
    }

    public static String genTxNo(int length, boolean characterContained) throws Exception {
        String txNo = null;
        switch(length) {
            case 16:
                if (characterContained) {
                    txNo = getTxNo16V2();
                } else {
                    txNo = getTxNo16();
                }
                break;
            case 17:
            case 18:
            case 21:
            case 22:
            case 23:
            case 24:
            case 26:
            default:
                throw new Exception("don't support length[" + length + "]");
            case 19:
                if (characterContained) {
                    throw new Exception("length [" + length + "] don't support generate txNo with charactes");
                }

                txNo = getTxNo19();
                break;
            case 20:
                if (characterContained) {
                    txNo = getTxNoV2();
                } else {
                    txNo = getTxNo();
                }
                break;
            case 25:
                if (characterContained) {
                    throw new Exception("length [" + length + "] don't support generate txNo with charactes");
                }

                txNo = getTxNo25();
                break;
            case 27:
                if (characterContained) {
                    throw new Exception("length [" + length + "] don't support generate txNo with charactes");
                }

                txNo = getTxNo27();
        }

        return txNo;
    }

    /** @deprecated */
    @Deprecated
    public static String getTxNo() {
        String timeString = sdf_minute.format(Calendar.getInstance().getTime());
        return timeString + RandomNumberGenerator.getRandomNumber(10);
    }

    /** @deprecated */
    @Deprecated
    public static String getTxNo16() {
        String timeString = sdf_day.format(Calendar.getInstance().getTime());
        return timeString + RandomNumberGenerator.getRandomNumber(10);
    }

    public static String getTxNo20V2() {
        String timeString = sdf_second2.format(Calendar.getInstance().getTime());
        return timeString + RandomNumberGenerator.getRandomNumber(6);
    }

    private static String getTxNoV2() throws Exception {
        String time = sdf_second2.format(Calendar.getInstance().getTime());
        String timeCoverted = time.substring(0, 4) + convert(time.substring(4));
        return timeCoverted + RandomNumberGenerator.getRandomCharAndNumber(11, true);
    }

    private static String getTxNo25() {
        String timeString = sdf_millisecond.format(Calendar.getInstance().getTime());
        return timeString + RandomNumberGenerator.getRandomNumber(10);
    }

    private static String getTxNo27() {
        String timeString = sdf_millisecond2.format(Calendar.getInstance().getTime());
        return timeString + RandomNumberGenerator.getRandomNumber(10);
    }

    private static String getTxNo16V2() throws Exception {
        String time = sdf_second.format(Calendar.getInstance().getTime());
        String timeCoverted = time.substring(0, 2) + convert(time.substring(2));
        return timeCoverted + RandomNumberGenerator.getRandomCharAndNumber(9, true);
    }

    private static String getTxNo19() {
        String timeString = sdf_minute.format(Calendar.getInstance().getTime());
        return timeString + RandomNumberGenerator.getRandomNumber(9);
    }

    public static String convert(String time) throws Exception {
        String timeConverted = "";

        for(int index = 0; index < time.length(); index += 2) {
            int num = Integer.parseInt(time.substring(index, index + 2));
            timeConverted = timeConverted + convertNumber2Character(num);
        }

        return timeConverted;
    }

    private static char convertNumber2Character(int num) throws Exception {
        if (num > 61) {
            throw new Exception("don't supprot digit larger than 61");
        } else {
            return CHARACTER_TABLE[num];
        }
    }
}
