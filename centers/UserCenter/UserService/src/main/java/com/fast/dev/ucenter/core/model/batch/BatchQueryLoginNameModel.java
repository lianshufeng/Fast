package com.fast.dev.ucenter.core.model.batch;

import com.fast.dev.ucenter.core.type.UserLoginType;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class BatchQueryLoginNameModel {

    //登录项
    private LoginItem[] items;


    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Accessors(chain = true)
    @Data
    public static class LoginItem {
        //登录类型
        private UserLoginType loginType;
        //登录名
        private String loginName;
    }

}
