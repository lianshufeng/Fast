package com.fast.dev.ucenter.core.helper.password;

import com.fast.dev.ucenter.core.type.PassWordEncodeType;

public abstract class PassWordExecute {

    /**
     * 校验
     *
     * @param salt
     * @param source
     * @param target
     * @return
     */
    public abstract boolean validate(String salt, String source, String target);


    /**
     * 编码
     *
     * @param salt
     * @param source
     * @return
     */
    public abstract String enCode(String salt, String source);

    public abstract PassWordEncodeType type();

}
