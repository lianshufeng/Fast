package com.fast.dev.ucenter.core.model;

import com.fast.dev.ucenter.core.type.UserMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 作者：练书锋
 * 时间：2018/8/28
 * 用户消息
 */
public class UserMessage implements Serializable {

    /**
     * 用户id
     */
    private String uid;

    /**
     * 用户令牌
     */
    private String uToken;

    /**
     * 消息类型
     */
    private UserMessageType type;

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

    public UserMessageType getType() {
        return type;
    }

    public void setType(UserMessageType type) {
        this.type = type;
    }


    public UserMessage() {
    }

    public UserMessage(String uid, String uToken, UserMessageType type) {
        this.uid = uid;
        this.uToken = uToken;
        this.type = type;
    }
}
