package com.fast.dev.pay.server.core.hb.sign;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class KKAES2 {
    public static String AES = "AES";
    private static KKAES2 instance = null;

    private KKAES2() {
    }

    public static KKAES2 getInstance() {
        if (instance == null) {
            instance = new KKAES2();
        }

        return instance;
    }

    public static byte[] encryptMode(byte[] keystr, byte[] text, Cipher cipher) throws Exception {
        String ivStr = "0000000000000000";
        byte[] keyBytes = new byte[16];
        byte[] ivBytes = new byte[16];
        byte[] v = ivStr.getBytes("UTF-8");
        int len = keystr.length;
        int len2 = v.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }

        if (len2 > ivBytes.length) {
            len2 = ivBytes.length;
        }

        System.arraycopy(keystr, 0, keyBytes, 0, len);
        System.arraycopy(v, 0, ivBytes, 0, len2);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(1, keySpec, ivSpec);
        byte[] results = cipher.doFinal(text);
        return results;
    }

    public static byte[] decryptMode(byte[] keystr, byte[] text, Cipher cipher, byte[] iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(keystr, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(2, keySpec, ivSpec);
        byte[] results = cipher.doFinal(text);
        return results;
    }
}
