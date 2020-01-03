package com.fast.dev.user.auth.dao.extend;

import java.util.Set;

public interface UserIdentityUpdateListDaoExtend {


    /**
     * 添加需要更新身份的用户
     *
     * @param userIds
     */
    void addUser(Set<String> userIds);


    /**
     * 添加需要更新身份的用户
     *
     * @param userIds
     */
    void addUser(String... userIds);


    /**
     * 获取需要更新用户身份的用户id
     *
     * @param limit
     * @return
     */
    Set<String> findAndeRemoveUpdateUser(int limit);


}
