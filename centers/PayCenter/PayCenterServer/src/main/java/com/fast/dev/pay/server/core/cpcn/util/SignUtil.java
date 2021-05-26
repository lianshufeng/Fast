package com.fast.dev.pay.server.core.cpcn.util;

import lombok.SneakyThrows;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class SignUtil {


    @SneakyThrows
    public static byte[] sign(PrivateKey privateKey,String data) {
        // 使用私钥加签
        byte[] signature = null;
        //用私钥给入参加签
        Signature sign = Signature.getInstance("SHA1WithRSA");
        sign.initSign(privateKey);
        sign.update(data.getBytes("UTF-8"));
        signature = sign.sign();
        return signature;
    }

    @SneakyThrows
    public static boolean verify(PublicKey publicKey,byte[] data, byte[] signature){
        Signature verifySign = Signature.getInstance("SHA1withRSA");
        verifySign.initVerify(publicKey);
        verifySign.update(data);
        return verifySign.verify(signature);
    }

}
