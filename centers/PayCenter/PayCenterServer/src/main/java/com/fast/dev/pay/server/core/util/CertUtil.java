package com.fast.dev.pay.server.core.util;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertUtil {


    /**
     * 读取证书序列号
     *
     * @param certData
     * @return
     */
    @SneakyThrows
    public static String readCertSerialNo(byte[] certData) {
        X509Certificate cert = x509Certificate(certData);
        cert.checkValidity();
        return cert.getSerialNumber().toString(16).toUpperCase();
    }


    /**
     * 读取证书到期时间
     *
     * @param certData
     * @return
     */
    @SneakyThrows
    public static long readCertValidTime(byte[] certData) {
        X509Certificate cert = x509Certificate(certData);
        cert.checkValidity();
        return cert.getNotAfter().getTime();
    }


    @SneakyThrows
    private static X509Certificate x509Certificate(byte[] certData) {
        @Cleanup ByteArrayInputStream inputStream = new ByteArrayInputStream(certData);
        CertificateFactory cf = CertificateFactory.getInstance("X509");
        return (X509Certificate) cf.generateCertificate(inputStream);
    }


}
