package com.fast.dev.ucenter.core.model;

import com.fast.dev.ucenter.core.type.TokenState;
import lombok.Data;

/**
 * 加强令牌
 */
@Data
public class StrongServiceTokenModel extends BasicServiceToken{
    public StrongServiceTokenModel(TokenState tokenState) {
        setTokenState(tokenState);
    }
}
