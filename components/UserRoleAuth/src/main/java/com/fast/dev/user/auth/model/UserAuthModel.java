package com.fast.dev.user.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthModel {

    //当前权限
    private Set<String> auths;

    //详情
    private Map<String, Object> details;

   //用户中心的id
    private String uid;

    //用户令牌
    private String uToken;


}
