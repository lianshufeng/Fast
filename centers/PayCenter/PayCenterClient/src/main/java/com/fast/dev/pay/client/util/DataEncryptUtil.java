package com.fast.dev.pay.client.util;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 数据加密工具
 */
public class DataEncryptUtil {


    /**
     * 加密
     *
     * @param datasource byte[]
     * @param password   String
     * @return byte[]
     */
    @SneakyThrows
    private static byte[] encrypt(byte[] datasource, String password) {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(password.getBytes("UTF-8"));
        KeyGenerator generator = KeyGenerator.getInstance("DES");
        generator.init(secureRandom);
        SecretKey secretKey = generator.generateKey();
        //Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("DES");
        //用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        //现在，获取数据并加密
        //正式执行加密操作
        return cipher.doFinal(datasource);
    }

    /**
     * 解密
     *
     * @param src      byte[]
     * @param password String
     * @return byte[]
     * @throws Exception
     */
    @SneakyThrows
    private static byte[] decrypt(byte[] src, String password) {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(password.getBytes("UTF-8"));
        KeyGenerator generator = KeyGenerator.getInstance("DES");
        generator.init(secureRandom);
        SecretKey secretKey = generator.generateKey();
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        // 真正开始解密操作
        return cipher.doFinal(src);
    }

    /**
     * 加密数据
     *
     * @param data
     * @param password
     * @return
     */
    public static String encrypt(String data, String password) {
        return Base64.getEncoder().encodeToString(encrypt(data.getBytes(StandardCharsets.UTF_8), password));
    }


    /**
     * 解密数据
     *
     * @param src
     * @param password
     * @return
     */
    @SneakyThrows
    public static String decrypt(String src, String password) {
        return new String(decrypt(Base64.getDecoder().decode(src), password), StandardCharsets.UTF_8);
    }


}
