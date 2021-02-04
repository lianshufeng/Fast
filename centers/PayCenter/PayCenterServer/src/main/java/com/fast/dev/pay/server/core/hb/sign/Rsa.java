package com.fast.dev.pay.server.core.hb.sign;

import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Rsa {
    public String KEY_ALGORITHM = "RSA";


    public Rsa() {
    }

    public byte[] encryptByPublicKey(byte[] data, String key, Cipher cipher) throws Exception {
        byte[] keyBytes = Base64.decode(key);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(this.KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        cipher.init(1, publicKey);
        byte[] output = cipher.doFinal(data);
        return output;
    }

    public byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64.decode(key);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(this.KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(2, privateKey);
        return cipher.doFinal(data);
    }



}
