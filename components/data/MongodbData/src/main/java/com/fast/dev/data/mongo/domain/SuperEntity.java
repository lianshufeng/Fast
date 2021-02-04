package com.fast.dev.data.mongo.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * 所有对象的父类
 *
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2018年1月5日
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class SuperEntity extends AbstractPersistable<String> implements Serializable {


    @Id
    private String id;


    /**
     * 创建时间
     */

    @Indexed
    @CreatedDate
    private Long createTime;

    /**
     * 修改时间
     */
    @Indexed
    @LastModifiedDate
    private Long updateTime;


}
