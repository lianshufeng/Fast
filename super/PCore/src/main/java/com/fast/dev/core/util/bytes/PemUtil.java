package com.fast.dev.core.util.bytes;

import com.fast.dev.core.util.text.TextUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 证书工具
 */
public class PemUtil {
    //每行的数量
    private static final int LineCount = 64;
    //编码
    private static final Charset charset = Charset.forName("UTF-8");
    //注释间隔符
    private static final String spaceStr = "-----";

    /**
     * 读取证书的内容为一行文本
     *
     * @param inputStream
     * @return
     */
    @SneakyThrows
    public static String read(InputStream inputStream) {
        String text = StreamUtils.copyToString(inputStream, charset);
        //过滤注释符号
        while (text.indexOf(spaceStr) > -1) {
            String line = spaceStr + TextUtil.subText(text, spaceStr, spaceStr, -1) + spaceStr;
            text = text.replaceAll(line, "");
        }
        //替换换行符
        return text.replaceAll("\r", "").replaceAll("\n", "");
    }


    /**
     * 读取证书的内容为一行文本
     *
     * @param file
     * @return
     */
    @SneakyThrows
    public static String read(File file) {
        @Cleanup FileInputStream fileInputStream = new FileInputStream(file);
        return read(fileInputStream);
    }


    /**
     * 写出数据到文本
     *
     * @param description
     * @return
     */
    public static String writeToText(String lineCer, String description) {
        StringBuilder sb = new StringBuilder();
        //第一行
        if (StringUtils.hasText(description)) {
            sb.append(spaceStr + "BEGIN " + description + spaceStr + "\n");
        }

        //每次读取前64字符
        StringBuilder source = new StringBuilder(lineCer);
        while (source.length() > LineCount) {
            sb.append(source.substring(0, LineCount) + "\n");
            source.delete(0, LineCount);
        }
        //读取最后的字符
        sb.append(source.substring(0, source.length()) + "\n");


        //最末一行
        if (StringUtils.hasText(description)) {
            sb.append(spaceStr + "END " + description + spaceStr + "\n");
        }

        return sb.toString();
    }

    /**
     * 写出数据到文本
     *
     * @param lineCer
     * @return
     */
    public static String writeToText(String lineCer) {
        return writeToText(lineCer, null);
    }


}
