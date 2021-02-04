package com.fast.dev.data.base.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataCleanProgress {

    //任务名
    private String taskName;

    //数据库实体
    private String entityName;

    //索引进度
    private long index;

    //总数量
    private long total;

    //    更新限制的时间
    private long limitUpdateTime;


}
