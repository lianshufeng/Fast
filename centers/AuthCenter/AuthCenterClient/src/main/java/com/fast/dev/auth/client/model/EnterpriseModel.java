package com.fast.dev.auth.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EnterpriseModel {


    /**
     * 企业id
     */
    private String id;


    /**
     * 企业名称，不能重复
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 标志
     */
    private String flag;


    /**
     * 应用秘钥
     */
    private String ak;


    /**
     * 扩展信息
     */
    private Map<String, String> info;


    /**
     * 禁用
     */
    private Boolean disable;


    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long createTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long updateTime;
}
