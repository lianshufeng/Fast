package com.fast.dev.auth.center.server.core.domain;

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

    /**
     * 关联的企业
     */
    @Indexed
    @DBRef(lazy = true)
    private Enterprise enterprise;


    /**
     * 角色名
     */
    @Indexed
    private String roleName;


    /**
     * 权限列表
     */
    @Indexed
    private Set<String> auth;


    /**
     * 备注信息
     */
    private String remark;


    /**
     * 身份
     */
    @Indexed
    private Set<String> identity;


    /**
     * 唯一索引
     */
    @Indexed(unique = true)
    private String uniqueIndex;

}
