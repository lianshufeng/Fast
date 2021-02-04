package com.fast.dev.auth.center.server.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class UserAuth extends SuperEntity {

    /**
     * 用户中心的id
     */
    @Indexed(unique = true)
    private String uid;


    /**
     * 用户权限
     */
    @Indexed
    private Set<String> auth;


}
