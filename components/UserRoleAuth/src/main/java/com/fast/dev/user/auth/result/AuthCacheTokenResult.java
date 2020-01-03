package com.fast.dev.user.auth.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AuthCacheTokenResult {

    //缓存权限与身份
    private String authCache;

    //用户的缓存值
    private String userCache;

}
