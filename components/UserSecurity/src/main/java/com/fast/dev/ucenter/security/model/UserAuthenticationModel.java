package com.fast.dev.ucenter.security.model;

import com.fast.dev.ucenter.core.model.UserTokenModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationModel extends UserTokenModel {


    /**
     * 角色列表
     */
    private Set<String> roles;


}
