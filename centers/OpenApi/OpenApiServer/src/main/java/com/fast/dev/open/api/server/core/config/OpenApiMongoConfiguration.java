package com.fast.dev.open.api.server.core.config;

import com.fast.dev.data.mongo.config.MongoConfiguration;
import com.fast.dev.data.token.config.ResourceTokenConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
//jpa必须
@EnableMongoRepositories("com.fast.dev.open.api.server.core.dao")
// 事务及配置BigDecimal
@Import({MongoConfiguration.class})
public class OpenApiMongoConfiguration {


}
