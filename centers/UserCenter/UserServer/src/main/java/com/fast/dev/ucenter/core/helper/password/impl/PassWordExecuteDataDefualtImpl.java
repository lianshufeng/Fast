package com.fast.dev.ucenter.core.helper.password.impl;

import com.fast.dev.ucenter.core.helper.password.PassWordExecute;
import com.fast.dev.ucenter.core.type.PassWordEncodeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

/**
 * 密码工具-默认
 */
@Slf4j
@Service
public class PassWordExecuteDataDefualtImpl extends PassWordExecute {


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
        String pd = DigestUtils.md5DigestAsHex(source.getBytes());
        return DigestUtils.md5DigestAsHex((salt + "_" + pd + "_" + salt).getBytes());
    }

    @Override
    public PassWordEncodeType type() {
        return PassWordEncodeType.Default;
    }


}
