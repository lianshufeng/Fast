package com.fast.dev.ucenter.core.model;

import com.fast.dev.ucenter.core.type.TokenState;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 * <p>
 * 用户登陆令牌
 */
@Data
@AllArgsConstructor
public class UserLoginToken extends BasicServiceToken {

    public UserLoginToken(TokenState tokenState) {
        setTokenState(tokenState);
    }
}
