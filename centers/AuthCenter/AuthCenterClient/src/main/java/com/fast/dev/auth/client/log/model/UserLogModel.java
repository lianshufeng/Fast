package com.fast.dev.auth.client.log.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * 用户日志模型
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class UserLogModel implements Serializable {

    //应用名
    private String appName;

    //浏览器的UA
    private String ua;

    //用户的ip
    private String ip;

    //企业id
    private String enterPriseId;

    //用户id
    private String uid;

    //手机号码
    private String phone;

    //行为
    private String action;

    //方法
    private String method;

    //方法对应的参数
    private Map<String, Object> parameter;

    //日志项目
    private Map<String, Object> items;

    //耗时
    private long costTime;

    //访问时间
    private long accessTime = System.currentTimeMillis();
}
