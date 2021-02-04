package com.fast.dev.auth.client.model;

import lombok.*;

import java.util.Collection;

/**
 * 企业角色用户模型
 */
@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class EnterpriseRoleUserModel {

    //企业id
    private String enterpriseId;

    //角色用户模型
    private Collection<RoleUserModel> roleUserModels;


}
