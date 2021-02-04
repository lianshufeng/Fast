package com.fast.build.helper.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * git 的用户信息配置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GitInfo {




    /**
     * 地址
     */
    private String url;


    /**
     * 帐号
     */
    private String userName;


    /**
     * 密码
     */
    private String passWord;


    /**
     * 描述
     */
    private String description;


}
