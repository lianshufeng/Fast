package com.fast.dev.auth.center.server.core.domain;

import com.fast.dev.auth.center.server.core.model.FamilyModel;
import com.fast.dev.auth.client.model.FamilyMember;
import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Set;

/**
 * 用户信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class User extends SuperEntity {


    /**
     * 用户id
     */
    @Indexed
    private String uid;

    /**
     * 关联的企业
     */
    @Indexed
    @DBRef(lazy = true)
    private Enterprise enterprise;


    /**
     * 扩展信息
     */
    private Map<String, String> info;


    /**
     * 唯一索引
     */
    @Indexed(unique = true)
    private String uniqueIndex;


    /**
     * 角色组
     */
    @Indexed
    private Set<String> roles;


    //家庭组副本信息
    private FamilyModel family;

}
