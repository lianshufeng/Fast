package com.fast.dev.core.util.net.apache;

import lombok.SneakyThrows;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * URL编码工具
 */
public class UrlEncodeUtil {

    private final static String[] NotEncodeCharset = new String[]{
            ":", "/", "\\", "&", "?", "="
    };
    private final static Set<Integer> NotEncodeAscii = Collections.synchronizedSet(new HashSet<>());

    static {
        for (String s : NotEncodeCharset) {
            try {
                NotEncodeAscii.add((int) s.getBytes("UTF-8")[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public static String encode(String url) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < url.length(); i++) {
            String str = url.substring(i, i + 1);
            int ascii = (int) str.getBytes("UTF-8")[0];
            // 0 - 9  , A - Z , a = z , ( NotEncodeCharset  )
            if ((ascii >= 48 && ascii <= 57) || (ascii >= 65 && ascii <= 90) || (ascii >= 97 && ascii <= 122) || (NotEncodeAscii.contains(ascii))) {
                sb.append(str);
            } else {
                sb.append(URLEncoder.encode(str, "UTF-8"));
            }
        }

        return sb.toString();
    }


}
