package com.fast.dev.auth.center.server.core.domain;

import com.fast.dev.auth.client.model.FamilyMember;
import com.fast.dev.auth.client.type.FamilyIdentity;
import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Set;

/**
 * 家庭
 */
@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Family extends SuperEntity {

    //家庭身份(一个成员有且只能加入一个家庭),唯一索引靠dao初始化创建
    private Set<FamilyMember> member;

    @Indexed
    @DBRef(lazy = true)
    private Enterprise enterprise;

    //拥有者,一个用户有且只能拥有一个家庭
    @Indexed
    private String owner;


    //拥有者的唯一索引(epId+ownner)
    @Indexed(unique = true)
    private String ownerUniqueIndex;

    //成员的唯一索引
    @Indexed(unique = true)
    private Set<String> memberUniqueIndex;


    /**
     * 扩展信息
     */
    private Map<String, Object> info;

}
