package com.fast.dev.promise.server.core.config;

import com.fast.dev.data.mongo.config.MongoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
// 重要
@EnableMongoRepositories(basePackages = {"com.fast.dev.promise.server.core.dao"})
@Import(MongoConfiguration.class)
public class MongoJpaConfig {
}
