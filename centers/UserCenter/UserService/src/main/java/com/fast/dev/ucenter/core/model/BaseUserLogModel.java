package com.fast.dev.ucenter.core.model;

import com.fast.dev.ucenter.core.type.UserLoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseUserLogModel {

    //用户id
    private String uid;

    //更新类型
    private UserLoginType userLoginType;

    //更换的登录名
    private String loginName;

    //创建记录的时间
    private long createTime;

}
