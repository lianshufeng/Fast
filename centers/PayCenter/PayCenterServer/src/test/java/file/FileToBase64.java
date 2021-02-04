package file;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.IOException;

public class FileToBase64 {

    private static String readWXFileBase64(String fileName) {
        File file = new File("E:/cert/微信支付/cert/1499934022_20200707_cert/" + fileName);
        return readBase64(file);
    }

    /**
     * 读取阿里支付的证书文件
     *
     * @param fileName
     * @return
     */
    private static String readAliFileBase64(String fileName) {
        File file = new File("C:/Users/Administrator/Documents/支付宝开放平台开发助手/CSR/" + fileName);
        return readBase64(file);
    }

    //    @SneakyThrows
    private static String readBase64(File file) {
        try {
            return Base64Utils.encodeToString(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Test
    public void ali_merchantPrivateKey() {
        System.out.println(readAliFileBase64("dzurl.top_私钥.txt"));
    }

    @Test
    public void ali_alipayCertPublicKey_RSA2() {
        System.out.println(readAliFileBase64("alipayCertPublicKey_RSA2.crt"));
    }

    @Test
    public void ali_alipayRootCert_crt() {
        System.out.println(readAliFileBase64("alipayRootCert.crt"));
    }

    @Test
    public void ali_appCertPublicKey_xxxx_crt() {
        System.out.println(readAliFileBase64("appCertPublicKey_2016102800774411.crt"));
    }


    @Test
    public void wx_apiclient_cert() {
        System.out.println(readWXFileBase64("apiclient_cert.pem"));
    }

    @Test
    public void wx_apiclient_key() {
        System.out.println(readWXFileBase64("apiclient_key.pem"));
    }

    @Test
    public void wx_apiclient_cert_p12() {
        System.out.println(readWXFileBase64("apiclient_cert.p12"));
    }

}
