package com.fast.dev.ucenter.core.model;

import com.fast.dev.ucenter.core.type.TokenState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者：练书锋
 * 时间：2018/8/30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterModel {

    /**
     * 状态
     */
    private TokenState state;


    /**
     * 用户id
     */
    private String uid;


    public UserRegisterModel(TokenState state) {
        this.state = state;
    }
}
