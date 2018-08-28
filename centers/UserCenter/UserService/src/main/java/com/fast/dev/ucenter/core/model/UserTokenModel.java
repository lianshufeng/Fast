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
    private String uToken;

    /**
     * 用户密钥
     */
    private String sToken;


    public TokenState getTokenState() {
        return tokenState;
    }

    public void setTokenState(TokenState tokenState) {
        this.tokenState = tokenState;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuToken() {
        return uToken;
    }

    public void setuToken(String uToken) {
        this.uToken = uToken;
    }

    public String getsToken() {
        return sToken;
    }

    public void setsToken(String sToken) {
        this.sToken = sToken;
    }

    public UserTokenModel(TokenState tokenState) {
        this.tokenState = tokenState;
    }

    public UserTokenModel() {

    }
}
