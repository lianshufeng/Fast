package com.fast.dev.ucenter.security.model;

import lombok.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 用户身份
 */
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentity implements Serializable {

    /**
     * 权限
     */
    @Getter
    @Setter
    private Set<String> auths;

    /**
     * 其他信息
     */
    @Getter
    @Setter
    private Map<String, Object> details;


    @Deprecated
    public Set<String> getRoles() {
        return auths;
    }
    @Deprecated
    public void setRoles(Set<String> roles) {
        this.auths = auths;
    }
}
