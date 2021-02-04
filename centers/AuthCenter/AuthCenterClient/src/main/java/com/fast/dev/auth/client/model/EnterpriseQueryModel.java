package com.fast.dev.auth.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 用户查询模型
 */
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseQueryModel {

    //查询语句
    private String mql;

    //关键词,如果为null则全部查询，否则只查询需要的字段
    private Set<String> fields;


}
