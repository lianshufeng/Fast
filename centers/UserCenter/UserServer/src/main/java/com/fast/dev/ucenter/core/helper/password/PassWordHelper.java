package com.fast.dev.ucenter.core.helper.password;

import com.fast.dev.ucenter.core.helper.password.type.PassWordEncodeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PassWordHelper {


    Map<PassWordEncodeType, PassWordExecute> _cahce = new HashMap<>();


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private void init() {
        for (PassWordExecute value : this.applicationContext.getBeansOfType(PassWordExecute.class).values()) {
            this._cahce.put(value.type(), value);
        }
    }


    /**
     * 校验
     *
     * @param salt
     * @param source
     * @param target
     * @return
     */
    public boolean validate(String salt, String source, String target, PassWordEncodeType passWordEncodeType) {
        passWordEncodeType = passWordEncodeType == null ? PassWordEncodeType.Default : passWordEncodeType;
        return this._cahce.get(passWordEncodeType).validate(salt, source, target);
    }


    /**
     * 编码
     *
     * @param salt
     * @param source
     * @return
     */
    public String enCode(String salt, String source, PassWordEncodeType passWordEncodeType) {
        passWordEncodeType = passWordEncodeType == null ? PassWordEncodeType.Default : passWordEncodeType;
        return this._cahce.get(passWordEncodeType).enCode(salt, source);
    }
}
