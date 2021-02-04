package com.fast.dev.auth.client.type;

import lombok.Getter;

/**
 * 权限常量类型
 */
public enum AuthConstantType {

    SuperAdmin("超级管理员", "超级管理员身份", "超级管理员角色"),

    Admin("管理员", "管理员身份", "管理员角色"),

    User("用户", "用户身份", "用户角色"),

    ;


    // 权限名
    @Getter
    private String authName;

    // 身份名
    @Getter
    private String identity;

    // 角色名
    @Getter
    private String roleName;

    AuthConstantType(String authName, String identity, String roleName) {
        this.authName = authName;
        this.identity = identity;
        this.roleName = roleName;
    }
}
