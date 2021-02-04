package com.fast.dev.auth.client.model;

import com.fast.dev.auth.client.type.AuthEventAction;
import com.fast.dev.auth.client.type.AuthEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * 权限事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AuthEventModel {

    /**
     * 事件类型
     */
    private AuthEventType type;


    /**
     * 事件动作
     */
    private AuthEventAction action;


    /**
     * 数据
     */
    private Map<String, Object> data;

}
