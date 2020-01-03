package com.fast.dev.user.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleModel {

    //校色名
    private String roleName;

    //备注
    private String remark;

    //权限列表
    private Set<String> auth;

    //身份列表
    private String identity;

    //权限的父类
    private String parent;
}
