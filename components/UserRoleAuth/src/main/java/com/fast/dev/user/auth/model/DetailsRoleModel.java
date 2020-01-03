package com.fast.dev.user.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailsRoleModel extends RoleModel {

    //父类
    private DetailsRoleModel parentRole;

    //继承的权限
    private Set<String> extendAuth;

}
