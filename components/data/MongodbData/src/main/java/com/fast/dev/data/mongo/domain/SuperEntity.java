package com.fast.dev.data.mongo.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;

/**
 * 所有对象的父类
 *
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2018年1月5日
 */
@Data
public abstract class SuperEntity implements Serializable {

    @Id
    private String id;

    // 创建时间
    @Indexed
    private long createTime;

    // 修改时间
    @Indexed
    private long updateTime;


}
