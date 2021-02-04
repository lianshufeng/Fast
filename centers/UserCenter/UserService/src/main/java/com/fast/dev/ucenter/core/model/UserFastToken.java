package com.fast.dev.ucenter.core.model;

import com.fast.dev.ucenter.core.type.TokenState;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 * <p>
 * 用户快捷令牌
 */
@Data
@AllArgsConstructor
public class UserFastToken extends BasicServiceToken {

    public UserFastToken(TokenState tokenState) {
        setTokenState(tokenState);
    }
}
