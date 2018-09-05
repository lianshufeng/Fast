package com.fast.dev.ucenter.core.service.extend;

import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.model.UpdatePassWordToken;
import com.fast.dev.ucenter.core.type.TokenState;
import com.fast.dev.ucenter.core.type.UserLoginType;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 */
public interface UserPassword {


    /**
     * 获取修改密码的令牌
     *
     * @param loginType
     * @param loginName
     * @param env
     * @return
     */
    public UpdatePassWordToken getUpdatePassWordToken(UserLoginType loginType, String loginName, TokenEnvironment env);


    /**
     * 修改密码
     *
     * @param token
     * @param code
     * @param passWord
     * @param newPassWord
     * @param env
     * @return
     */
    public TokenState updatePassWord(String token, String code, String passWord, String newPassWord, TokenEnvironment env);

}
