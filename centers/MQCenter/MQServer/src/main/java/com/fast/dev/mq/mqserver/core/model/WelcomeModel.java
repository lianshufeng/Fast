package com.fast.dev.mq.mqserver.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WelcomeModel {

    /**
     * uuid
     */
    private String token;


    /**
     * 服务器地址
     */
    private String[] hosts;


    /**
     * 账号
     */
    private String userName;

    /**
     * 密码
     */
    private String passWord;


}
