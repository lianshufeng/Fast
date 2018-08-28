package com.fast.dev.ucenter.security.model;

import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.core.type.TokenState;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * 用户权限
 */
public class UserAuth extends UserTokenModel {


    /**
     * 角色列表
     */
    private Set<String> roles;


    /**
     * 详情
     */
    private Map<String, Object> details;


    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}
