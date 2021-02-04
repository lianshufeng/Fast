package com.fast.dev.auth.center.server.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * 企业
 */


@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class Enterprise extends SuperEntity {

    /**
     * 企业名称，不能重复
     */
    @Indexed(unique = true)
    private String name;


    /**
     * 扩展信息
     */
    private Map<String, String> info;


    /**
     * 禁用
     */
    @Indexed
    private Boolean disable;


    /**
     * 备注
     */
    private String remark;


    /**
     * 标志
     */
    @Indexed
    private String flag;


    /**
     * Api key
     */
    @Indexed(unique = true, sparse = true)
    private String ak;


    /**
     * Secret Key
     */
    private String sk;


    public static Enterprise build(String id) {
        Enterprise enterprise = new Enterprise();
        enterprise.setId(id);
        return enterprise;
    }


}
