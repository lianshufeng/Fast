package com.fast.dev.ucenter.core.model;

import com.fast.dev.ucenter.core.type.TokenState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 * 用户注册令牌
 */
@Data
@NoArgsConstructor
public class UserRegisterToken extends BasicServiceToken {

    public UserRegisterToken(TokenState tokenState) {
        setTokenState(tokenState);
    }
}
