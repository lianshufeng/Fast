package com.fast.dev.ucenter.core.core.config;

import com.fast.dev.data.mongo.config.MongoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories("com.fast.dev.ucenter.core.dao")
@Import(MongoConfiguration.class)
public class MongoConfig {


}
