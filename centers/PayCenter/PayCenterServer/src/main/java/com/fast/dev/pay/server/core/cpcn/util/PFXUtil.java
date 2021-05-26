package com.fast.dev.pay.server.core.cpcn.util;


import com.fast.dev.pay.server.core.cpcn.conf.CPCNConf;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class PFXUtil  {


    @SneakyThrows
    public static PrivateKey getPrivateKey(InputStream inputStream,String pwd){
        //获取PKCS12密钥库
        KeyStore ks = KeyStore.getInstance("PKCS12");
        char[] nPassword = null;
        if ((pwd == null) || pwd.trim().equals("")) {
            nPassword = null;
        } else {   //把密码字符串转为字符数组
            nPassword = pwd.toCharArray();
        }
        //将.pfx证书信息加载密钥库
        ks.load(inputStream, nPassword);

        inputStream.close();
        Enumeration enumeration = ks.aliases();
        String keyAlias = null;
        if (enumeration.hasMoreElements())
        {   //获取证书别名
            keyAlias = (String)enumeration.nextElement();
        }
        return (PrivateKey) ks.getKey(keyAlias,nPassword);
    }


    @SneakyThrows
    public static PublicKey getPublicKey(InputStream in){
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate)cf.generateCertificate(in);
        //获取PKCS12密钥库
        PublicKey publicKey = cert.getPublicKey();
        in.close();
        return publicKey;

    }



   /* public static void main(String[] args)
    {

        PrivateKey prikey = getPrivateKey();
        PublicKey pubkey = getPublicKey();
        System.out.println("public key = " + new String(Base64.encode(pubkey.getEncoded())));
        System.out.println("private key = " + new String(Base64.encode(prikey.getEncoded())));
       *//* //证书路径
        final String KEYSTORE_FILE     = "C:\\Users\\zZ\\Desktop\\中金支付\\test.pfx";
        //证书密码
        final String KEYSTORE_PASSWORD = "cfca1234";


        try
        {
            //获取PKCS12密钥库
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(KEYSTORE_FILE);

            // If the keystore password is empty(""), then we have to set
            // to null, otherwise it won't work!!!
            char[] nPassword = null;
            if ((KEYSTORE_PASSWORD == null) || KEYSTORE_PASSWORD.trim().equals(""))
            {
                nPassword = null;
            }
            else
            {   //把密码字符串转为字符数组
                nPassword = KEYSTORE_PASSWORD.toCharArray();
            }
            //将.pfx证书信息加载密钥库
            ks.load(fis, nPassword);
            fis.close();
            //证书类型
            System.out.println("keystore type=" + ks.getType());


            Enumeration enum1 = ks.aliases();
            String keyAlias = null;
            if (enum1.hasMoreElements())
            {   //获取证书别名
                keyAlias = (String)enum1.nextElement();
                System.out.println("alias=[" + keyAlias + "]");
            }


            System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
            Certificate cert = ks.getCertificate(keyAlias);
            PublicKey pubkey = cert.getPublicKey();


            System.out.println("cert class = " + cert.getClass().getName());
            System.out.println("cert = " + cert);
            System.out.println("public key = " + new String(Base64.encode(pubkey.getEncoded())));
            System.out.println("private key = " + new String(Base64.encode(prikey.getEncoded())));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*//*
    }*/

}
