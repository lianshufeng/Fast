package com.fast.dev.mq.mqserver.core.client;

import com.fast.dev.core.util.JsonUtil;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class ActiveMQClient {


    @Autowired
    private JmsTemplate jmsTemplate;


    /**
     * 发送到topic
     */
    public void sendData(String topic, Object parm) {
        jmsTemplate.convertAndSend(new ActiveMQTopic(topic), JsonUtil.toJson(parm));
    }


}
