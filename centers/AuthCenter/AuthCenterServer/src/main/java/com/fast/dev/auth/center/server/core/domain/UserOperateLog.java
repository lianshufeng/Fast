package com.fast.dev.auth.center.server.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Document
@AllArgsConstructor
@NoArgsConstructor
public class UserOperateLog extends SuperEntity {

    /**
     * 用户中心的id
     */
    @Indexed
    @DBRef(lazy = true)
    private User user;

    /**
     * 应用名
     */
    @Indexed
    private String appName;


    /**
     * 业务名
     */
    @Indexed
    private String serviceName;

    /**
     * 请求的URL
     */
    @Indexed
    private String uri;

    /**
     * 用户ip
     */
    @Indexed
    private String ip;

    /**
     * User Agent
     */
    private String ua;

    /**
     * 请求参数
     */
    private Map<String, Object> parameter;


    /**
     * 过期时间
     */
    @Indexed(expireAfterSeconds = 60 * 60 * 24 * 14)
    private Date expirationTime;

}
