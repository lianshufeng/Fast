package com.fast.dev.pay.server.core.hb.helper;

import com.fast.dev.core.util.os.SystemUtil;
import com.fast.dev.pay.server.core.hb.model.CertModel;
import com.fast.dev.pay.server.core.util.CmdUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * 证书生成工具
 */
@Component
public class CertHelper {


    private static final String[] privateKeyToolsCmd = {
            "keytool", "-genkeypair", "-validity", "$validity", "-keyalg", "$keyalg", "-keysize", "$keySize", "-alias", "$alias", "-storepass", "$storepass", "-keystore", "$keystore", "-dname", "$dname"
    };


    private static final String[] publicKeyToolsCmd = {
            "keytool", "-certreq", "-alias", "%s", "-storepass", "%s", "-keystore", "%s", "-file", "%s"
    };


    /**
     * 获取私钥命令行
     *
     * @return
     */
    public String[] getPrivateKeyToolsCmd() {
        return Arrays.copyOf(privateKeyToolsCmd, privateKeyToolsCmd.length);
    }

    /**
     * 获取公钥命令行
     *
     * @return
     */
    public String[] getPublicKeyToolsCmd() {
        return Arrays.copyOf(publicKeyToolsCmd, publicKeyToolsCmd.length);
    }


    /**
     * 制作公钥
     *
     * @param privateInputStream
     * @param passWord
     * @param publicOutputStream
     */
    @SneakyThrows
    public MakeCertState makePublicCert(InputStream privateInputStream, String passWord, String alias, OutputStream publicOutputStream) {


        @Cleanup("delete") File privateKeyFile = File.createTempFile("cert_", ".jks");
        privateKeyFile.delete();

        @Cleanup("delete") File publicKeyFile = File.createTempFile("cert_", ".p10");
        publicKeyFile.delete();

        @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(privateKeyFile);
        StreamUtils.copy(privateInputStream, fileOutputStream);


        //生产公钥
        String[] publicKeyCmd = getPublicKeyToolsCmd();
        publicKeyCmd[3] = alias;
        publicKeyCmd[5] = passWord;
        publicKeyCmd[7] = privateKeyFile.getAbsolutePath();
        publicKeyCmd[9] = publicKeyFile.getAbsolutePath();

        CmdUtil.execute(publicKeyCmd);


        MakeCertState state = publicKeyFile.exists() && publicKeyFile.length() > 0 ?
                MakeCertState.Success : MakeCertState.Error;
        if (state == MakeCertState.Success) {
            @Cleanup FileInputStream keyPathFileInputStream = new FileInputStream(publicKeyFile);
            StreamUtils.copy(keyPathFileInputStream, publicOutputStream);
        }

        return state;
    }


    /**
     * 制作私钥
     *
     * @param certModel
     */
    @SneakyThrows
    public MakeCertState makePrivateCert(CertModel certModel, OutputStream privateOutputStream) {
        //校验
        if (!StringUtils.hasText(certModel.getStorePassWord())) {
            return MakeCertState.PassWordNotNUll;
        }
        if (!StringUtils.hasText(certModel.getAlias())) {
            return MakeCertState.aliasNotNull;
        }

        //生产临时文件，自动删除
        @Cleanup("delete") File privateKeyFile = File.createTempFile("cert_", ".jks");
        privateKeyFile.delete();

        //生产秘钥
        String dName = String.format("CN=%s, OU=%s ,O=%s, L=%s, S=%s, C=%s",
                certModel.getFirstLastName(),
                certModel.getOrganizational(),
                certModel.getOrganization(),
                certModel.getCityLocality(),
                certModel.getProvince(),
                certModel.getCountryCode()
        );


        String[] privateKeyCmd = getPrivateKeyToolsCmd();
        privateKeyCmd[3] = String.valueOf(certModel.getValidity());
        privateKeyCmd[5] = String.valueOf(certModel.getKeyalg());
        privateKeyCmd[7] = String.valueOf(certModel.getKeySize());
        privateKeyCmd[9] = String.valueOf(certModel.getAlias());
        privateKeyCmd[11] = String.valueOf(certModel.getStorePassWord());
        privateKeyCmd[13] = privateKeyFile.getAbsolutePath();
//        privateKeyCmd[15] = "\""+dName+"\"";
        privateKeyCmd[15] = dName;
        CmdUtil.execute(privateKeyCmd);


        //私钥文件存在
        MakeCertState makeCertState = privateKeyFile.exists() && privateKeyFile.length() > 0 ? MakeCertState.Success : MakeCertState.Error;
        if (makeCertState == MakeCertState.Success) {
            @Cleanup FileInputStream fileInputStream = new FileInputStream(privateKeyFile);
            StreamUtils.copy(fileInputStream, privateOutputStream);
        }
        return makeCertState;
    }

    /**
     * 返回的状态
     */
    public static enum MakeCertState {
        //成功
        Success,

        //失败
        Error,

        //密码不能为空
        PassWordNotNUll,

        //别名不能为空
        aliasNotNull,
    }


}
