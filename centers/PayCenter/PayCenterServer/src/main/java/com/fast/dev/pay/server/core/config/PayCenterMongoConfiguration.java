package com.fast.dev.pay.server.core.config;

import com.fast.dev.data.mongo.config.MongoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
//jpa必须
@EnableMongoRepositories({
        "com.fast.dev.pay.server.core.dao",
        "com.fast.dev.pay.server.core.general.dao",
        "com.fast.dev.pay.server.core.hb.dao"
})
// 事务及配置BigDecimal
@Import(MongoConfiguration.class)
public class PayCenterMongoConfiguration {


}
