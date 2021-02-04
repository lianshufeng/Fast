package com.fast.dev.auth.client.model;

import lombok.*;

import java.util.Set;

/**
 * 角色用户模型
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RoleUserModel extends RoleModel {


    //用户模型
    private Set<UserModel> userModel;


}
