package com.fast.dev.ucenter.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 * 用户令牌
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToken extends BaseToken {


    /**
     * 密钥
     */
    private String secret;


}
