package com.fast.dev.ucenter.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseToken extends SuperEntity {

    /**
     * 令牌
     */
    private String token;


    /**
     * 访问次数
     */
    private int accessCount;

}
