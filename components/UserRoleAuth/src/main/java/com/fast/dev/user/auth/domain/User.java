package com.fast.dev.user.auth.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends SuperEntity {
    //用户中心的id
    @Indexed(unique = true)
    private String uid;

    //是否禁用
    @Indexed
    private boolean disable;


    // 身份
    @Indexed
    private Set<String> identities;


}
