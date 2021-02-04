package com.fast.dev.open.api.server.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Api文档
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class ApiDoc extends SuperEntity {

    /**
     * 接口名称
     */
    @Indexed
    private String name;

    /**
     * 接口描述
     */
    private String remark;

    /**
     * 请求的接口
     */
    @Indexed
    private String url;


}
