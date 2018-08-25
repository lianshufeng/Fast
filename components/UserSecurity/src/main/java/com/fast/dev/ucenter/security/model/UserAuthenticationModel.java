package com.fast.dev.ucenter.security.model;

import com.fast.dev.ucenter.core.model.UserTokenModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserAuthenticationModel extends UserTokenModel {


    /**
     * 角色列表
     */
    private Set<String> roles;


    /**
     * 其他信息
     */
    private Map<String, Object> other;




}
