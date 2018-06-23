package com.fast.dev.data.mongo.config;

import com.fast.dev.data.mongo.config.converts.BigDecimalToDecimal128Converter;
import com.fast.dev.data.mongo.config.converts.Decimal128ToBigDecimalConverter;
import com.mongodb.MongoClientOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 集成自动配置
 */

@Configuration
public class MappingMongoConverterConfiguration {

    /**
     * 自定义转换器
     * @param mongoDbFactory
     * @param mongoMappingContext
     * @return
     * @throws Exception
     */
    @Bean
    @ConditionalOnMissingBean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory mongoDbFactory, MongoMappingContext mongoMappingContext) throws Exception {
        DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        List<Object> list = new ArrayList<>();
        list.add(new BigDecimalToDecimal128Converter());//自定义的类型转换器
        list.add(new Decimal128ToBigDecimalConverter());//自定义的类型转换器
        converter.setCustomConversions(new MongoCustomConversions(list));
        return converter;
    }


}
