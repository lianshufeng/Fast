package demo.simple.config;

import com.fast.dev.data.mongo.config.MongoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
// 重要
@EnableMongoRepositories(basePackages = {"demo.simple.dao"})
@Import(MongoConfiguration.class)
public class MongoJpaConfig {
}
