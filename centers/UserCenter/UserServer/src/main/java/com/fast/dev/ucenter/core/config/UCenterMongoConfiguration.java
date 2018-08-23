package com.fast.dev.ucenter.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
//jpa必须
@EnableMongoRepositories("com.fast.dev.ucenter.core.dao")
// 事务及配置BigDecimal
@Import(UCenterMongoConfiguration.class)
//扫描工具包
@ComponentScan("com.fast.dev.data.mongo")
public class UCenterMongoConfiguration {


}
