package com.fast.dev.auth.client.bean;

import com.fast.dev.auth.client.model.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 具有权限信息的用户，推荐直接继承
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser {


    /**
     * 角色
     */
    private Set<String> roles = new HashSet<>();


    /**
     * 身份
     */
    private Set<String> identity = new HashSet<>();


    /**
     * 功能权限
     */
    private Set<String> auth = new HashSet<>();


    /**
     * 用户基本信息
     */
    private UserModel user;


}
