package com.fast.dev.ucenter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 * <p>
 * 用户登陆令牌
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginToken {

    /**
     * 令牌
     */
    private String token;


    /**
     * 登陆时间
     */
    private String loginTime;


    /**
     * 登陆校验
     */
    private LoginValidate loginValidate;


}
