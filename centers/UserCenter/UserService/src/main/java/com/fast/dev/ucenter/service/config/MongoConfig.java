package com.fast.dev.ucenter.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories("com.fast.dev.ucenter.service.dao")
public class MongoConfig {



}
