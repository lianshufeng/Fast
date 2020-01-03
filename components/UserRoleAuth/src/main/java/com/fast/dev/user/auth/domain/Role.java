package com.fast.dev.user.auth.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * 角色表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Role extends SuperEntity {

    //校色名
    @Indexed(unique = true)
    private String roleName;


    //权限列表
    @Indexed
    private Set<String> auth;


    //父类
    @Indexed
    @DBRef(lazy = true)
    private Role parent;


    //身份列表
    @Indexed
    private String identity;


    //备注
    private String remark;


}
