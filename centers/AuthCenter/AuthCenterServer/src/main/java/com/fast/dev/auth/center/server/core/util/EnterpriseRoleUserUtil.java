package com.fast.dev.auth.center.server.core.util;

import com.fast.dev.auth.client.model.EnterpriseRoleUserModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EnterpriseRoleUserUtil {


    /**
     * 获取用户id
     *
     * @param enterpriseRoleUserModel
     * @return
     */
    public static Collection<String> getUserId(EnterpriseRoleUserModel enterpriseRoleUserModel) {
        Set<String> userIds = new HashSet<>();
        if (enterpriseRoleUserModel.getRoleUserModels() == null) {
            return userIds;
        }
        enterpriseRoleUserModel.getRoleUserModels().forEach((roleUserModel) -> {
            Optional.ofNullable(roleUserModel.getUserModel()).ifPresent((userModels) -> {
                userIds.addAll(userModels.stream().map((userModel) -> {
                    return userModel.getUid();
                }).collect(Collectors.toSet()));
            });
        });
        return userIds;
    }


}
