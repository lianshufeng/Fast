package com.fast.dev.data.base.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSyncDetails {


    /**
     * 数据实体
     */
    private Class<? extends AbstractPersistable> entity;


    /**
     * 数据id
     */
    private Set<String> ids;


    /**
     * 通知次数
     */
    private int noticeCount;

}
