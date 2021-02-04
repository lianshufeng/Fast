package com.fast.dev.pay.server.core.hb.sign;

import java.io.ByteArrayInputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.Enumeration;

public class CertUtil {
    public CertUtil() {
    }

    public static KeyPair exKeyFromJks(byte[] pk, String password) throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pk);
        ks.load(byteArrayInputStream, password.toCharArray());
        byteArrayInputStream.close();
        Enumeration<?> enumas = ks.aliases();
        String alias = null;
        if (enumas.hasMoreElements()) {
            alias = (String)enumas.nextElement();
        }

        Key key = ks.getKey(alias, password.toCharArray());
        if (key instanceof PrivateKey) {
            Certificate cert = ks.getCertificate(alias);
            PublicKey publicKey = cert.getPublicKey();
            return new KeyPair(publicKey, (PrivateKey)key);
        } else {
            return null;
        }
    }


    public static String getPrivateKey(byte[] pk, String password) throws Exception {
        KeyPair kp = exKeyFromJks(pk, password);
        if (kp != null) {
            PrivateKey private1 = kp.getPrivate();
            return Base64.getEncoder().encodeToString(private1.getEncoded());
        } else {
            return null;
        }
    }

    public static byte[] getPrivateKeybyte(byte[] pk, String password) throws Exception {
        KeyPair kp = exKeyFromJks(pk, password);
        if (kp != null) {
            PrivateKey private1 = kp.getPrivate();
            return private1.getEncoded();
        } else {
            return null;
        }
    }

    public static String getPublicKey(byte[] pk, String password) throws Exception {
        KeyPair kp = exKeyFromJks(pk, password);
        PublicKey public1 = kp.getPublic();
        return Base64.getEncoder().encodeToString(public1.getEncoded());

    }
}
