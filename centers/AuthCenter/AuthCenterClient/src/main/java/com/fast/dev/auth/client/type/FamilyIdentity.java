package com.fast.dev.auth.client.type;

import lombok.Getter;

/**
 * 家庭身份
 */
public enum FamilyIdentity {
    /**
     * 监护人
     */
    Guardian("监护人"),


    /**
     * 被监护人
     */
    Ward("被监护人");


    FamilyIdentity(String name) {
        this.name = name;
    }

    @Getter
    private String name;


}
