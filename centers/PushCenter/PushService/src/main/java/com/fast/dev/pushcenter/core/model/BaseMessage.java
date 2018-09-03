package com.fast.dev.pushcenter.core.model;

import com.fast.dev.pushcenter.core.type.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 作者：练书锋
 * 时间：2018/9/3
 * 用户消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseMessage implements Serializable {

    /**
     * 模版id
     */
    private String templateId;


    /**
     * 内容数据
     */
    private Map<String, Object> content;


    /**
     * 消息类型
     */
    private MessageType messageType;


}
