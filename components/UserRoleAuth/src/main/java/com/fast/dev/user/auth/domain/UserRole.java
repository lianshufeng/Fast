package com.fast.dev.user.auth.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class UserRole extends SuperEntity {

    //用户
    @Indexed
    @DBRef(lazy = true)
    private User user;

    //角色
    @Indexed
    @DBRef(lazy = true)
    private Role role;


}
