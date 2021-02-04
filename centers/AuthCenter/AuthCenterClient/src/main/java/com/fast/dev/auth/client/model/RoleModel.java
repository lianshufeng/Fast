package com.fast.dev.auth.client.model;

import lombok.*;

import java.util.Set;

@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RoleModel {

    //角色id
    private String id;

    //角色名
    private String roleName;

    //备注
    private String remark;

    //权限列表
    private Set<String> auth;

    //身份
    private Set<String> identity;

    //企业id
    private String enterpriseId;


}
