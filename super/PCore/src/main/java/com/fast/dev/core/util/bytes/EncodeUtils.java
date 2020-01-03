package com.fast.dev.core.util.bytes;

import lombok.Cleanup;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.BitSet;

public class EncodeUtils {
    private static int BYTE_SIZE = 8;


    /**
     * 获取流的编码，请注意流的一次消费
     *
     * @param inputStream
     * @return
     */
    @SneakyThrows
    public static EnCodeType getEncode(InputStream inputStream) {
        @Cleanup BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        return getEncode(bufferedInputStream, true);
    }


    /**
     * 通过文件缓存流获取编码集名称，文件流必须为未曾
     *
     * @param bis
     * @return
     * @throws Exception
     */
    @SneakyThrows
    public static EnCodeType getEncode(@NonNull BufferedInputStream bis, boolean ignoreBom) {
        bis.mark(0);
        EnCodeType encodeType = null;
        byte[] head = new byte[3];
        bis.read(head);
        if (head[0] == -1 && head[1] == -2) {
            encodeType = EnCodeType.UTF16;
        } else if (head[0] == -2 && head[1] == -1) {
            encodeType = EnCodeType.Unicode;
        } else if (head[0] == -17 && head[1] == -69 && head[2] == -65) { //带BOM
            if (ignoreBom) {
                encodeType = EnCodeType.UTF8;
            } else {
                encodeType = EnCodeType.UTF8_BOM;
            }
        } else if ("Unicode".equals(encodeType)) {
            encodeType = EnCodeType.UTF16;
        } else if (isUTF8(bis)) {
            encodeType = EnCodeType.UTF8;
        } else {
            encodeType = EnCodeType.GBK;
        }
        return encodeType;
    }

    /**
     * 是否是无BOM的UTF8格式，不判断常规场景，只区分无BOM UTF8和GBK
     *
     * @param bis
     * @return
     */
    @SneakyThrows
    private static boolean isUTF8(@NonNull BufferedInputStream bis) {
        bis.reset();

        //读取第一个字节
        int code = bis.read();
        do {
            BitSet bitSet = convert2BitSet(code);
            //判断是否为单字节
            if (bitSet.get(0)) {//多字节时，再读取N个字节
                if (!checkMultiByte(bis, bitSet)) {//未检测通过,直接返回
                    return false;
                }
            } else {
                //单字节时什么都不用做，再次读取字节
            }
            code = bis.read();
        } while (code != -1);
        return true;
    }

    /**
     * 检测多字节，判断是否为utf8，已经读取了一个字节
     *
     * @param bis
     * @param bitSet
     * @return
     */
    @SneakyThrows
    private static boolean checkMultiByte(@NonNull BufferedInputStream bis, @NonNull BitSet bitSet) {
        int count = getCountOfSequential(bitSet);
        byte[] bytes = new byte[count - 1];//已经读取了一个字节，不能再读取
        bis.read(bytes);
        for (byte b : bytes) {
            if (!checkUtf8Byte(b)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测单字节，判断是否为utf8
     *
     * @param b
     * @return
     */
    @SneakyThrows
    private static boolean checkUtf8Byte(byte b) {
        BitSet bitSet = convert2BitSet(b);
        return bitSet.get(0) && !bitSet.get(1);
    }

    /**
     * 检测bitSet中从开始有多少个连续的1
     *
     * @param bitSet
     * @return
     */
    private static int getCountOfSequential(@NonNull BitSet bitSet) {
        int count = 0;
        for (int i = 0; i < BYTE_SIZE; i++) {
            if (bitSet.get(i)) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }


    /**
     * 将整形转为BitSet
     *
     * @param code
     * @return
     */
    private static BitSet convert2BitSet(int code) {
        BitSet bitSet = new BitSet(BYTE_SIZE);

        for (int i = 0; i < BYTE_SIZE; i++) {
            int tmp3 = code >> (BYTE_SIZE - i - 1);
            int tmp2 = 0x1 & tmp3;
            if (tmp2 == 1) {
                bitSet.set(i);
            }
        }
        return bitSet;
    }


    /**
     * 文件编码
     */
    public enum EnCodeType {
        UTF8("UTF-8"),
        UTF8_BOM("UTF-8_BOM"),
        UTF16("UTF-16"),
        GBK("GBK"),
        Unicode("Unicode"),

        ;

        @Getter
        private String codeName;

        EnCodeType(String codeName) {
            this.codeName = codeName;
        }
    }


}
