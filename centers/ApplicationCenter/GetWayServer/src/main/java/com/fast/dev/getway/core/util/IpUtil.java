package com.fast.dev.getway.core.util;

public class IpUtil {

    /**
     * ip地址转换为十进制
     *
     * @return
     */
    public static long ipv4ToLong(String ip) {
        String[] ips = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < ips.length; i++) {
            int power = 3 - i;
            result += Integer.parseInt(ips[i]) * Math.pow(256, power);
        }
        return result;
    }


    /**
     * long 转换为 ip
     *
     * @param i
     * @return
     */
    public static String longToIpv4(long i) {
        return ((i >> 24) & 0xFF) +
                "." + ((i >> 16) & 0xFF) +
                "." + ((i >> 8) & 0xFF) +
                "." + (i & 0xFF);

    }


}
