package com.fast.dev.data.jpa.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA 配置
 */

//允许自动装载数据源
@Import(DataSourceAutoConfiguration.class)

//允许使用jpa注解
@EnableJpaAuditing

//允许事务管理
@EnableTransactionManagement
public class JpaDataConfiguration {
}
