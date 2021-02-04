package com.fast.dev.ucenter.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.ucenter.core.type.UserLoginType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 基础用户的日志
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class BaseUserLog extends SuperEntity {

    //关联的用户
    @DBRef(lazy = true)
    private BaseUser baseUser;

    //更新类型
    private UserLoginType userLoginType;

    //更换的登录名
    private String loginName;


}
