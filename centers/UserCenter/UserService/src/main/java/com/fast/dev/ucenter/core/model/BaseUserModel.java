package com.fast.dev.ucenter.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseUserModel implements Serializable {
    /**
     * 用户id
     */
    private String id;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

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


}
