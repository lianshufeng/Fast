package com.fast.dev.auth.center.server.core.dao.extend;

public interface UserRoleDaoExtend {


    /**
     * 查询该用户是否存在该角色中
     *
     * @param uid
     * @param roleId
     * @return
     */
    boolean existsByUserAndRole(String uid, String roleId);


}
