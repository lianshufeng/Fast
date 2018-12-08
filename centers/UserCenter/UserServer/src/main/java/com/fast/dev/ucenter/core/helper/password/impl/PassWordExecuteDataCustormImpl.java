package com.fast.dev.ucenter.core.helper.password.impl;

import com.fast.dev.core.util.bytes.BytesUtil;
import com.fast.dev.ucenter.core.helper.password.PassWordExecute;
import com.fast.dev.ucenter.core.helper.password.type.PassWordEncodeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Base64;

/**
 * 密码工具-自定义
 */
@Slf4j
@Service
public class PassWordExecuteDataCustormImpl extends PassWordExecute {


    /**
     * 校验
     *
     * @param salt
     * @param source
     * @param target
     * @return
     */
    @Override
    public boolean validate(String salt, String source, String target) {
        if (StringUtils.isEmpty(salt) || StringUtils.isEmpty(source) || StringUtils.isEmpty(target)) {
            return false;
        }
        return enCode(salt, source).equals(target);
    }


    /**
     * 编码
     *
     * @param salt
     * @param source
     * @return
     */
    @Override
    public String enCode(String salt, String source) {
        salt = source + "{" + salt + "}";
        byte[] bin = org.apache.commons.codec.digest.DigestUtils.sha256(salt.getBytes());
        for (int i = 1; i < 5000; ++i) {
            try {
                byte[] newBin = BytesUtil.merge(bin, salt.getBytes());
                bin = DigestUtils.sha256(newBin);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Base64.getEncoder().encodeToString(bin);
    }

    @Override
    public PassWordEncodeType type() {
        return PassWordEncodeType.Custom;
    }


}
