package com.fast.dev.ucenter.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToken {

    /**
     * 用户id
     */
    private String uid;

    /**
     * 用户令牌
     */
    private String uToken;

    /**
     * 用户密钥
     */
    private String sToken;

    /**
     * 到期时间
     */
    private long expireTime;


}
