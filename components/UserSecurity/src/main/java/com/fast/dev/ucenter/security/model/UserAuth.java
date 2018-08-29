package com.fast.dev.ucenter.security.model;

import com.fast.dev.ucenter.core.model.UserTokenModel;

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


    /**
     * 创建时间
     */
    private long createTime;


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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
