package com.fast.dev.core.util.encrypt;


import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class AesUtil {

    public final static String DefaultCharSet = "UTF-8";
    private static final String KEY_ALGORITHM = "AES";
    private static final String KEY_SIGN = "MD5";
    private final static String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";


    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return
     */
    @SneakyThrows
    public static byte[] encrypt(final String content, String password) {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(password));
        return cipher.doFinal(content.getBytes(DefaultCharSet));
    }


    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    @SneakyThrows
    public static byte[] decrypt(byte[] content, String password) {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getKey(password));
        return cipher.doFinal(content);
    }

    /**
     * 构建key
     *
     * @param key
     * @return
     * @throws Exception
     */
    @SneakyThrows
    private static SecretKeySpec getKey(String key) {
        return new SecretKeySpec(md5(key), KEY_ALGORITHM);
    }

    /**
     * sha1
     *
     * @param data
     * @return
     */
    @SneakyThrows
    private static byte[] md5(String data) {
        MessageDigest messageDigest = MessageDigest.getInstance(KEY_SIGN);
        messageDigest.update(data.getBytes());
        return messageDigest.digest();
    }


}
