package com.fast.dev.auth.center.server.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 系统任务
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class SystemTask extends SuperEntity {


    /**
     * 任务名
     */
    @Indexed(unique = true, sparse = true)
    private String taskName;


    /**
     * 次数
     */
    @Indexed
    private Long count;


    /**
     * 过期时间
     */
    @Indexed(expireAfterSeconds = 0)
    private Date ttl;

}
