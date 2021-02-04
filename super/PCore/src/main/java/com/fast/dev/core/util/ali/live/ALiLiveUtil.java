package com.fast.dev.core.util.ali.live;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

public class ALiLiveUtil {


    private static String SourcesTemplate = "%s-%s-%s-%s-%s";
    private static String OutputTemplate = "%s-%s-%s-%s";


    /**
     * 计算出直播流的 auth_key
     *
     * @param uri
     * @param expiresSecond
     * @param key
     * @return
     */
    public static String getAuthkey(String uri, int expiresSecond, String key) {
        long time = ((long) System.currentTimeMillis() / 1000) + expiresSecond;
        String uid = "0";
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String source = String.format(SourcesTemplate, uri, time, uuid, uid, key);
        String hash = DigestUtils.md5Hex(source);
        return String.format(OutputTemplate, String.valueOf(time), uuid, uid, hash);
    }

//    public static void main(String[] args) {
//        System.out.println("rtmp://live.meishuwa.cn/app/test1?auth_key=" + (getAuthkey("/app/test1", 60, "s3dp9vB8qy")));
//        System.out.println("rtmp://pull.live.meishuwa.cn/app/test1?auth_key=" + (getAuthkey("/app/test1", 60, "WuCPsoegfD")));
//    }


//    public static void main(String[] args) {
//        var serviceName = "/app/test1";
//        System.out.println("rtmp://push.live.aiyilearning.com" + serviceName + "?auth_key=" + (getAuthkey(serviceName, 360, "***")));
//        System.out.println("rtmp://pull.live.aiyilearning.com" + serviceName + "?auth_key=" + (getAuthkey(serviceName, 360, "***")));
//    }


}
