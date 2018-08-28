package com.fast.dev.ucenter.core.model;

import com.fast.dev.ucenter.core.type.UserMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者：练书锋
 * 时间：2018/8/28
 * 用户消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMessage {

    /**
     * 用户id
     */
    private String uid;


    /**
     * 消息类型
     */
    private UserMessageType type;

}
