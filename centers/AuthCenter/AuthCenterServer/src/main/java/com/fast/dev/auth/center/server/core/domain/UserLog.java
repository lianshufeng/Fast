package com.fast.dev.auth.center.server.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;


/**
 * 用户日志
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class UserLog extends SuperEntity {

    //应用名
    @Indexed
    private String appName;

    //浏览器的UA
    @Indexed
    private String ua;

    //用户的ip
    @Indexed
    private String ip;

    //企业id
    @Indexed
    private String enterPriseId;

    //用户id
    @Indexed
    private String uid;

    //手机号码
    @Indexed
    private String phone;

    //行为
    @Indexed
    private String action;

    //方法
    @Indexed
    private String method;

    //方法对应的参数
    private Map<String, Object> parameter;

    //日志项目
    private Map<String, Object> items;

    //耗时
    @Indexed
    private long costTime;

    //访问时间
    @Indexed
    private long accessTime;

    // time to live
    @Indexed(expireAfterSeconds = 0)
    private Date TTL;
}
