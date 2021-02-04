package com.fast.dev.ucenter.core.model;

import com.fast.dev.ucenter.core.type.TokenState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 * 基础的功能令牌
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class BasicServiceToken implements Serializable {

    /**
     *   令牌状态
     */
    private TokenState tokenState;

    /**
     * 令牌
     */
    private String token;


    /**
     * 登陆校验
     */
    private RobotValidate robotValidate;


}
