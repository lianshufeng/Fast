package com.fast.dev.ucenter.security.model;

import com.fast.dev.ucenter.core.model.UserTokenModel;
import lombok.*;

import java.util.Map;
import java.util.Set;

/**
 * 用户权限
 */

public class UserAuth extends UserTokenModel {

    private Set<String> auths;

    /**
     * 详情
     */
    private Map<String, Object> details;


    /**
     * 创建时间
     */
    private long createTime;


    public Set<String> getAuths() {
        return auths;
    }

    public void setAuths(Set<String> auths) {
        this.auths = auths;
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
