package com.fast.dev.mq.mqserver.core.config;

import com.fast.dev.mq.mqserver.core.conf.MQConf;
import com.fast.dev.mq.mqserver.core.constant.MQConstant;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Destination;

@Configuration
public class TopicConfiguration {

    @Autowired
    private MQConf mqConfig;


    @Bean
    public Destination requetQueue() {
        ActiveMQTopic activeMQTopic = new ActiveMQTopic(MQConstant.RequetTopic);
        return activeMQTopic;
    }


}
