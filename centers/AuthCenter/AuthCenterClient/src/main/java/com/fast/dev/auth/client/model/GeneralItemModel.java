package com.fast.dev.auth.client.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public abstract class GeneralItemModel {
    /**
     * 名称
     */
    private String name;


    /**
     * 备注
     */
    private String remark;

}
