package com.fast.dev.mq.mqserver.core.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "mq")
public class MQConf {


    /**
     * 外部服务器
     */
    private String[] outerHosts;

    /**
     * 外部用的账号
     */
    private String outerUserName = "user";

    /**
     * 外部用的密码
     */
    private String outerPassWord = "password";


    /**
     * 网关的名称
     */
    private String gatewayServiceName = "GateWayServer";


    /**
     * 消息的消费者队列
     */
    private int messageThreadPoolCount = 200;


    /**
     * 令牌的存活时间
     */
    private long tokenLifeTime = 1000 * 60 * 60 * 24;


}
