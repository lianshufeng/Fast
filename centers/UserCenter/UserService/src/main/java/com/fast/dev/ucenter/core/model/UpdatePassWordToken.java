package com.fast.dev.ucenter.core.model;

import com.fast.dev.ucenter.core.type.TokenState;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者：练书锋
 * 时间：2018/9/5
 * 修改密码的令牌
 */
@Data
@NoArgsConstructor
public class UpdatePassWordToken extends BasicServiceToken {
    public UpdatePassWordToken(TokenState tokenState) {
        setTokenState(tokenState);
    }
}

