package com.fast.dev.pay.server.core.hb.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseTaskProcess extends SuperEntity {

    //任务id
    @Indexed(unique = true)
    private String taskId;

    //执行顺序
    @Indexed
    private long count;

    //过期时间
    @Indexed(expireAfterSeconds = 0)
    private Date TTL;



}
