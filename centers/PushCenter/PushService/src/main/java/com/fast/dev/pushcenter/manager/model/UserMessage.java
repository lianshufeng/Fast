package com.fast.dev.pushcenter.manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者：练书锋
 * 时间：2018/9/3
 * 用户消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMessage extends  BaseMessage {

    /**
     * 用户id
     */
    private String[] uid;


}
