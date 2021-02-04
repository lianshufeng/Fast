package com.fast.dev.auth.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 企业用户
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EnterpriseUserModel extends UserModel {


    /**
     * 关联的企业
     */
    private String enterpriseId;


    /**
     * 用户所在企业的角色的id
     */
    private Set<RoleModel> roles;


    /**
     * 家庭组副本信息
     */
    private UserQueryFamilyModel family;

}
