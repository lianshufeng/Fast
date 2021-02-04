package com.fast.dev.data.mongo.data.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;


@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class DataCleanTask extends SuperEntity {

    //任务名
    @Indexed(unique = true)
    private String taskName;

    //数据库实体
    @Indexed
    private String entityName;

    //总数量
    private long total;


    //活跃时间
    @Indexed
    private long activeTime;


    //更新限制的时间
    @Indexed
    private long limitUpdateTime;

    //任务完成的进度
    private BigDecimal progress;

    @Indexed
    private String uuid;


}
