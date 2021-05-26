package com.fast.dev.core.util.net;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 取ip的工具
 */
public class IPUtil {


    /**
     * 可能出现用户真实ip的头
     */
    public final static String[] headNames = new String[]{
            "X-FORWARDED-FOR",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };


    /**
     * 获取远程ip
     *
     * @param request
     * @return
     */
    public static String getRemoteIp(HttpServletRequest request) {
        final Map<String,String> header = new HashMap();
        Arrays.stream(headNames).forEach((name) -> {
            header.put(name, request.getHeader(name));
        });
        return getRemoteIp(header, request.getRemoteAddr());
    }


    /**
     * 获取远程ip
     *
     * @return
     */
    public static String getRemoteIp(Map<String,String> headers, String defaultRemoteAddr) {
        for (String name : headNames) {
            Object obj = headers.get(name);
            if (obj == null || "".equals(String.valueOf(obj))) {
                continue;
            }

            String ip = String.valueOf(obj);
            int at = ip.indexOf(",");
            if (at > -1) {
                ip = ip.substring(0, at);
            }
            return ip;
        }
        return defaultRemoteAddr;
    }


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
