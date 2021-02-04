package com.fast.dev.ucenter.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 * 用户令牌
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserToken extends BaseToken {


    /**
     * 用户id
     */
    private String uid;


    /**
     * 密钥
     */
    private String secret;


}
