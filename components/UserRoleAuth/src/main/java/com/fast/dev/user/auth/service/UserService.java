package com.fast.dev.user.auth.service;

import com.fast.dev.user.auth.domain.User;

public interface UserService<T extends User> {


    /**
     * 查询或存储用户
     *
     * @param uid
     * @return
     */
    T findAndSaveUser(String uid);


}
