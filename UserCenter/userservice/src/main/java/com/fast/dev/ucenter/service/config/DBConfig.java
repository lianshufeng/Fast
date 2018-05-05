package com.fast.dev.ucenter.service.config;

import com.fast.dev.component.mongodb.conf.MongodbConfig;
import com.fast.dev.component.mongodb.configuration.MongodbConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBConfig extends MongodbConfiguration {

    @Override
    public MongodbConfig mongodbConfig() {
        MongodbConfig mongodbConfig = new MongodbConfig();
        mongodbConfig.setDbName("test");
        mongodbConfig.setHost(new String[]{"127.0.0.1:27017"});
        return mongodbConfig;
    }
}
