package com.fast.dev.pay.server.core.hb.sign;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class Envelope {
    private Rsa rsa = new Rsa();
    private KKAES2 kkAES2 = KKAES2.getInstance();

    public Envelope() {
    }

    public String getPassword(int count, boolean letters, boolean numbers) {
        Random random = new Random();
        return random.random(count, 0, 0, letters, numbers, (char[])null);
    }

    private String getPassword(int count) {
        return this.getPassword(count, true, true);
    }

    public byte[] seal_Sessionkey(X509Certificate cert, Cipher cipher, SecretKey sessionKey) throws Exception {
        byte[] o_EncSessionkey = new byte[256];

        try {
            PublicKey publickey = cert.getPublicKey();
            byte[] bPublickey = publickey.getEncoded();
            String sPublkey = Base64.getEncoder().encodeToString(bPublickey);
            byte[] encodedData = this.rsa.encryptByPublicKey(sessionKey.getEncoded(), sPublkey, cipher);
            System.arraycopy(encodedData, 0, o_EncSessionkey, 0, encodedData.length);
            return o_EncSessionkey;
        } catch (Exception var9) {
            throw new Exception("seal_Sessionkey error:" + var9.getMessage());
        }
    }

    public Map seal_Orignal(String i_symmAlgo, byte[] Orginal, Cipher cipher) throws Exception {
        try {
            Map map = new HashMap();
            byte[] key = this.getPassword(16).getBytes();
            SecretKey sessionKey = new SecretKeySpec(key, i_symmAlgo);
            map.put("sessionKey", sessionKey);
            map.put("o_Secret", KKAES2.encryptMode(sessionKey.getEncoded(), Orginal, cipher));
            return map;
        } catch (Exception var7) {
            throw new Exception("seal_Orignal error:" + var7.getMessage());
        }
    }

    public byte[] open_Orginal(byte[] sessionKey, byte[] enc_Orginal, Cipher cipher, byte[] iv) throws Exception {
        byte[] o_Orginal = (byte[])null;
        o_Orginal = KKAES2.decryptMode(sessionKey, enc_Orginal, cipher, iv);
        return o_Orginal;
    }

    public byte[] open_SessionKey(byte[] enc_SessionKey, byte[] keyFile) throws Exception {
        try {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyFile);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            byte[] bPrivatekey = priKey.getEncoded();
            String sPrivatekey = Base64.getEncoder().encodeToString(bPrivatekey);
            byte[] o_Sessionkey = this.rsa.decryptByPrivateKey(enc_SessionKey, sPrivatekey);
            return o_Sessionkey;
        } catch (Exception var9) {
            var9.printStackTrace();
            throw new Exception("open_SessionKey error:" + var9.getMessage());
        }
    }


}
