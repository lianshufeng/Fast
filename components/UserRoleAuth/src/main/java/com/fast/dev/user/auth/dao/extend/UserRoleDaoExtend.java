package com.fast.dev.user.auth.dao.extend;

public interface UserRoleDaoExtend {


    /**
     * 查询该用户是否存在该角色中
     *
     * @param userId
     * @param roleName
     * @return
     */
    boolean existsByUserAndRole(String userId, String roleName);


}
