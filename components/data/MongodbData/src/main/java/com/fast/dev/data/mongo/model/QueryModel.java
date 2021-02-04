package com.fast.dev.data.mongo.model;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 查询模型
 */
@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class QueryModel {

    //mongo的查询语句
    private String mql;

    //关键词,如果为null则全部查询，否则只查询需要的字段
    private Set<String> fields;


}
