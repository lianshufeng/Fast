package com.fast.dev.user.auth.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户身份更新列表
 */
@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentityUpdateList extends SuperEntity {


    @Indexed(unique = true)
    private String userId;


    @Indexed
    private String uuid;

}
