package com.fast.dev.mq.mqserver.core.config;

import com.fast.dev.mq.mqserver.core.conf.MQConf;
import com.fast.dev.mq.mqserver.core.constant.MQConstant;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import javax.jms.ConnectionFactory;

@Configuration
public class ConsumerConfiguration {

    @Value("${spring.activemq.broker-url}")
    private String host;

    @Value("${spring.activemq.user}")
    private String userName;

    @Value("${spring.activemq.password}")
    private String passWord;


    @Autowired
    private MQConf mqConf;


    /**
     * 配置预期量
     *
     * @return
     */
    @Bean
    public ActiveMQPrefetchPolicy activeMQPrefetchPolicy() {
        ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
        prefetchPolicy.setTopicPrefetch(mqConf.getMessageThreadPoolCount() * 2);
        prefetchPolicy.setDurableTopicPrefetch(mqConf.getMessageThreadPoolCount() * 2);
        prefetchPolicy.setMaximumPendingMessageLimit(mqConf.getMessageThreadPoolCount() * 2);
        return prefetchPolicy;
    }


    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(userName, passWord, host);
        activeMQConnectionFactory.setPrefetchPolicy(activeMQPrefetchPolicy());
        //采用异步投递消息
        activeMQConnectionFactory.setAlwaysSyncSend(true);
        //优化自动确认
        activeMQConnectionFactory.setOptimizeAcknowledge(true);
        return activeMQConnectionFactory;
    }


    @Bean(name = MQConstant.TopicListenerContainerFactory)
    public JmsListenerContainerFactory topicListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }


}