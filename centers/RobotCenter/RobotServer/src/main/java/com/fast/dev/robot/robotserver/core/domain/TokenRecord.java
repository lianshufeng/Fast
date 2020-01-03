package com.fast.dev.robot.robotserver.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * token的记录表
 */
@Data
public abstract class TokenRecord extends SuperEntity {

    @Indexed(unique = true)
    private String token;

    //过期时间
    @Indexed
    private long expireTime;

}
