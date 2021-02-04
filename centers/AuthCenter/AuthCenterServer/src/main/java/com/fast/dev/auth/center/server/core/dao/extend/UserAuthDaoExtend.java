package com.fast.dev.auth.center.server.core.dao.extend;

import java.util.Set;

public interface UserAuthDaoExtend {

    /**
     * 添加用户的权限
     *
     * @param uid
     * @param authNames
     */
    boolean addAuth(String uid, String... authNames);


    /**
     * 删除用户的权限
     *
     * @param uid
     * @param authNames
     * @return
     */
    boolean removeAuth(String uid, String... authNames);


    /**
     * 是否存在权限
     *
     * @param uid
     * @param authName
     * @return
     */
    boolean existAuth(String uid, String authName);


    /**
     * 查询用户的权限
     * @param uid
     * @return
     */
    Set<String> findUserAuth(String uid);

}
