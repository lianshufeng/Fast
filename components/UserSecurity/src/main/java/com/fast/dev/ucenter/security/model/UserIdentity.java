package com.fast.dev.ucenter.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * 用户身份
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentity {

    /**
     * 角色列表
     */
    private Set<String> roles;

    /**
     * 其他信息
     */
    private Map<String, Object> details;


}
