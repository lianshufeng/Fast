package com.fast.dev.pay.server.core.hb.sign;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class EnvApplication {
    private CertificateFactory cf = null;


    public EnvApplication() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            this.cf = CertificateFactory.getInstance("X.509", "BC");
            System.out.println("EnvApplication初始化成功!");
        } catch (Exception var2) {
            System.out.println("EnvApplication初始化异常：" + var2.getMessage());
        }

    }

    public String getCert(FileInputStream fileInputStream) {
        String result = "";
        try {
            byte[] b = new byte[fileInputStream.available()];
            fileInputStream.read(b);
            fileInputStream.close();
            result = Base64.getEncoder().encodeToString(b);
        } catch (Exception var6) {
            System.out.println("获取证书失败：" + var6.getMessage());
        }

        return result;

    }

    public String makeEnvelope(String cer, byte[] data) {
        String base64Res = "";
        try {
            byte[] certBytes = Base64.getDecoder().decode(cer);
            InputStream inputStream = new ByteArrayInputStream(certBytes);
            X509Certificate cert = (X509Certificate)this.cf.generateCertificate(inputStream);
            X509Certificate[] certs = new X509Certificate[]{cert};
            JPKcs7 jPkcs7 = new JPKcs7();
            Cipher sessionKeyCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            Cipher sealOrignalChipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] resBytes = jPkcs7.makePKCS7ENC(certs, data, KKAES2.AES, sessionKeyCipher, sealOrignalChipher);
            base64Res = Base64.getEncoder().encodeToString(resBytes);
            return base64Res;
        } catch (Exception var14) {
            var14.printStackTrace();
            System.out.println("信封加密失败：" + var14.getMessage());
            return base64Res;
        }
    }

    public String openEnvelope(byte[] pk, String keyPasswd, String data) {
        try {
            byte[] keyFile = CertUtil.getPrivateKeybyte(pk, keyPasswd);
            JPKcs7 jPkcs7 = new JPKcs7();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            byte[] orginal = jPkcs7.openPKCS7ENC(keyFile, data, cipher);
            return new String(orginal);
        } catch (Exception var10) {
            System.out.println("信封解密失败：" + var10.getMessage());
            return null;
        }
    }

    public String signJson(String digest, byte[] pk, String password) throws Exception {
        digest = SerialUtils.fromJson(digest);
        String privateKey = CertUtil.getPrivateKey(pk, password);
        String charset = "utf-8";
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(priKey);
            signature.update(digest.getBytes(charset));
            byte[] signed = signature.sign();
            return Base64.getEncoder().encodeToString(signed);
        } catch (Exception var10) {
            var10.printStackTrace();
            return null;
        }
    }

    public boolean verifyJson(String digest, String signed, byte[] pk,String pwd) throws Exception {
        digest = SerialUtils.fromJson(digest);
        String publicKey = CertUtil.getPublicKey(pk, pwd);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.getDecoder().decode(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(pubKey);
            signature.update(digest.getBytes("utf-8"));
            boolean bverify = signature.verify(Base64.getDecoder().decode(signed));
            return bverify;
        } catch (Exception var9) {
            var9.printStackTrace();
            return false;
        }
    }



//    public static void main(String[] args) throws Exception {
//        EnvApplication envApplication = new EnvApplication();
//        String json = "{\"userMobile\": \"18682630458\",\"remarkFiled1\": null,\"remarkFiled2\": null,\"mchtId\": \"HX1200000000001\",\"mchtNo\": \"HX1200000000001\",\"tradeNo\": \"2007301126204125311\",\"pcsDate\": \"20200730112621\",\"mac\": \"00-15-5D-F9-0E-82\",\"occurAdd\": \"172.30.160.1\",\"phoneNub\": null,\"ID\": null,\"IMSI\": null,\"ICCD\": null,\"GPS\": null}";
//        System.out.println("请求参数" + json);
//        String certS  = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpNSUlEYWpDQ0FsS2dBd0lCQWdJRVhYZFBNakFOQmdrcWhraUc5dzBCQVFzRkFEQndNUXN3Q1FZRFZRUUdFd0pEVGpFUU1BNEdBMVVFQnhNSFFtVnBhbWx1WnpFWE1CVUdBMVVFQ2hNT2QzZDNMbWg0WWk1amIyMHVZMjR4RURBT0JnTlZCQWdUQjBKbGFXcHBibWN4Q3pBSkJnTlZCQXNUQWtsVU1SY3dGUVlEVlFRREV3NTNkM2N1YUhoaUxtTnZiUzVqYmpBZUZ3MHhPVEE1TVRBd05qVXlNalphRncweU5EQTVNRGd3TnpJeU1qWmFNRnN4Q3pBSkJnTlZCQVlUQWtOT01Rd3dDZ1lEVlFRS0RBTklXRUl4RURBT0JnTlZCQWNNQjBKbGFVcHBibWN4RURBT0JnTlZCQWdNQjBKbGFVcHBibWN4RERBS0JnTlZCQXNNQTBoWVFqRU1NQW9HQTFVRUF3d0RhWEJ6TUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF3bDVyTG05QThicFY0V0V0Sm9hQzJmQlduRnZSMjBUb3BIeWF1eEZ0OTZTQW03TUtnSVJiVElDakV4VGFKakYzaEpzSWRwRm12RGdxSUprQlNkWDBKQ21vdDZhcXlDbVFQMCs1YUh1cHNKUlhuakxIam9OL3J5ZnJwWG9KUTJZaFlHZURpU1ZnTkk0TDVZM1BqejI0SmJ1QnFiN3ZVVXJtRzJ5VS9qOUJxNEpqYXlMb0NyM3RlL3BIK0RiU2orMjN6Wk5oOUIzZUJabEFnVDh6TmtOV2pFUFZXUlZPK1dCMHJKYmoybVBCVkNzdGhmVE0zR082dmZxMUtFRExIWS94czYvcWQxZTgvUDRKN0F0VmEyT3hwZEVwM2RZb0lsQ01KKytzQW5HalhRbmE4VENyVThoZmRiOEJnQ3hkU3pvaUEvekRMUXpOSkpVOGRkdCs3R0FVcXdJREFRQUJveUV3SHpBZEJnTlZIUTRFRmdRVVdOS3BQUkxkWEI5ZGlvREJ6WmpYUWFJcmtDMHdEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBQ29mbnlZcStNdUx1a3hPWE5TTmJUMXE3NGhib0pldmZITHg3ZUVqWWRQMXZXRU04anRLL3VrN2dUYVFLeDF5UEdYbXpjV2drYWd0Qi9HZWsvSGdyaDFoNnN0NGQvV1o3d01XS2FPS0tRbUVKZ1N5RkcvbjQrbEJ6WTZ2RThqcXBYZ1lGQ292S2E5djdqMHAzcGFOQVpGOTBtZ0h6MXVSenYzU3JjUk5IN2VmcE1qSklpSGU2ZXltdkdmbGp4Q3hEWVl5Y0t2cGlJMW5GanpUUjJGdEdSTjhCT2Z2eXc1Z0daZDlINFYrTy9GZk5PRmQzeVQ0RnRxRnM0L2tZcXcvRUZEaWx4WGZDQmp4Q1AxRGdOelhERXowYWx0dVlIWUZsQ1pHa01mb2Uza3AvZ3NGN2VBb0JxWkNtQ2FvN1l5VGJNTGJpNytSQWU5aTIvTUk2UkdKalljPQ0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQ==";
//        String encryption = envApplication.makeEnvelope(certS, json.getBytes("UTF-8"));
//        System.out.println("数字信封加密结果:" + encryption);
//        encryption = "MIICTQYJKoZIhvcNAQcDoIICPjCCAjoCAQAxggGUMIIBkAIBADB4MHAxCzAJBgNVBAYTAkNOMRAwDgYDVQQHEwdCZWlqaW5nMRcwFQYDVQQKEw53d3cuaHhiLmNvbS5jbjEQMA4GA1UECBMHQmVpamluZzELMAkGA1UECxMCSVQxFzAVBgNVBAMTDnd3dy5oeGIuY29tLmNuAgRe/XqmMA0GCSqGSIb3DQEBAQUABIIBAC7mhSnqWrUna7Lbujr3LxCIDYP3KrNJ8BCxHupwHcYWCHKQRxPAr9kq0FwdTmArlMsSBNoggOfKziL14ChJ5vuvY580oquNYnrj+3NR7N2bw1WEnfjMxWncSwDGMH7hPGgOHgs2u5ys2rvTbMo6wAb4j3e7VZqmHHcp4D0acpT1YTozM/WpxxMQ5cJuSA+zMhZF7+g5yjgVszVw7AawweLTYLlSKtj0tDzLpsoZymuPamhYlYWHcwnzB5BI5u+AYvDqAuuyhV8uzqgA4J8fUXysYEwbGwGmmM2d5GxDSl6EPsKEWVYCPaFXtXVSQ1Mc8f74eVj0GYvLsZGofG9pZCswgZwGCSqGSIb3DQEHATAdBglghkgBZQMEAQIEEEHnLJHga9dBL8TcpMKOLVWAcDdxE31Vcm5YddBMBGQY1yUUkC+WRecUJIgBWYoctrF7+bAnvnWKuF6Q8UkWhGsyVzNO+YI4oYvj4e7wHxk9Oi/OPwNPNRlidCvsw2R3RGA4E1I11DvZDtTCaE45DBMacRY+R6VlaQUvhwFrKE2v2L0=";
//        FileInputStream jks = new FileInputStream("D:/hb/kayak.jks");
//        byte[] byt = new byte[jks.available()];
//        jks.read(byt);
//        String result12 = envApplication.openEnvelope(byt, "hmjy1234", encryption);
//        System.out.println("数字信封解密结果:" + result12);
//        String signed = envApplication.signJson(json, byt, "hmjy1234");
//        System.out.println("signed:" + signed);
//        boolean doCheck = envApplication.verifyJson(json, signed, byt,"hmjy1234");
//        System.out.println(doCheck);
//    }
}
