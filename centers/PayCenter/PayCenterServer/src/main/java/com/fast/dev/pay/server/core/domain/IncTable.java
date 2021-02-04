package com.fast.dev.pay.server.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncTable extends SuperEntity {

    /**
     * 业务名
     */
    @Indexed(unique = true)
    private String serviceName;

    /**
     * inc计数器
     */
    @Indexed
    private long count;


    /**
     * 自动到期
     */
    @Indexed(expireAfterSeconds = 0)
    private Date TTL;
}
