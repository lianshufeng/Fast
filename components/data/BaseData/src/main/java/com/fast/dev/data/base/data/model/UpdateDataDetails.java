package com.fast.dev.data.base.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * 更新数据详情
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class UpdateDataDetails {


    /**
     * 数据实体
     */
    private Class<? extends AbstractPersistable> entity;


    /**
     * 数据id
     */
    private String[] ids;


}
