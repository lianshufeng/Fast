package com.fast.dev.ucenter.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories("com.fast.dev.ucenter.service.dao")
@ComponentScan("com.fast.dev.data.mongo")
public class MongoConfig {



}
