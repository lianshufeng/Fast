package com.fast.dev.data.token.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 资源令牌
 */
@Data
@Document
@NoArgsConstructor
public class ResourceToken extends SuperEntity {

    //资源类型
    @Indexed
    private ResourceType type;


    //任务名
    @Indexed
    private String resourceName;


    //统计工具
    @Indexed
    private long counter;


    //唯一索引
    @Indexed(unique = true, sparse = true)
    private String uniqueIndex;


    //过期时间
    @Indexed(expireAfterSeconds = 0)
    private Date TTL;


    public static enum ResourceType {
        /**
         * 系统
         */
        System,

        /**
         * 用户
         */
        User

    }
}
