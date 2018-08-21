package com.fast.dev.ucenter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    /**
     * 用户id
     */
    private String uid;


    /**
     * 用户真实姓名
     */
    private String realname;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 身份证
     */
    private String idCard;


    /**
     * 邮箱
     */
    private String mail;

    /**
     * 电话号码
     */
    private String phone;


    /**
     * 其他数据
     */
    private Map<String, Object> other;


}
