package com.fast.dev.ucenter.core.model;

import com.fast.dev.ucenter.core.type.TokenState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenModel implements Serializable {

    /**
     * 令牌状态
     */
    private TokenState tokenState;

    /**
     * 用户id
     */
    private String uid;

    /**
     * 用户令牌
     */
    private String token;

    /**
     * 用户密钥
     */
    private String secret;


    public UserTokenModel(TokenState tokenState) {
        this.tokenState = tokenState;
    }
}
