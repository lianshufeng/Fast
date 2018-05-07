package com.fast.dev.component.mongodb.configuration;

import com.fast.dev.component.mongodb.conf.MongodbConfig;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * mongodb 的配置
 *
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2017年10月9日
 */
@Configuration
public class MongodbConfiguration {

    //Mongodb的配置
    @Resource
    private MongodbConfig mongodbConfig;

    @Bean
    public MongoDbFactory mongoDbFactory() {
        // 数据库账号
        String userName = mongodbConfig.getUserName();
        // 数据库密码
        String passWord = mongodbConfig.getPassWord();
        // 数据库名
        String dbName = mongodbConfig.getDbName();
        // 服务端地址
        List<ServerAddress> serverAddressList = buildServerAddress(mongodbConfig);
        // 客户端配置
        MongoClientOptions mongoClientOptions = buildMongoClientOptions(mongodbConfig);
        // 创建mognodb的客户端
        MongoClient mongoClient = buildMongoClient(serverAddressList, dbName, userName, passWord, mongoClientOptions);
        // 创建mongodb工厂
        MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient, dbName);
        return mongoDbFactory;
    }


    @Bean
    public GridFsTemplate gridFsTemplate() {
        MongoDbFactory mongoDbFactory = mongoDbFactory();
        MongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory),
                new MongoMappingContext());
        GridFsTemplate gridFsTemplate = new GridFsTemplate(mongoDbFactory, converter);
        return gridFsTemplate;
    }

    /**
     * 配置 MongoTemplate
     *
     * @return
     */
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDbFactory());
    }


    /**
     * 创建mongodb的客户端
     *
     * @param serverAddressList
     * @param dbName
     * @param userName
     * @param passWord
     * @param mongoClientOptions
     * @return
     */
    private static MongoClient buildMongoClient(List<ServerAddress> serverAddressList, String dbName, String userName,
                                                String passWord, MongoClientOptions mongoClientOptions) {
        MongoClient mongoClient;
        if (!StringUtils.isEmpty(userName)) {
            MongoCredential mongoCredential = MongoCredential.createCredential(userName, dbName,
                    passWord.toCharArray());
            mongoClient = new MongoClient(serverAddressList, mongoCredential, mongoClientOptions);
        } else {
            mongoClient = new MongoClient(serverAddressList, mongoClientOptions);
        }
        return mongoClient;
    }

    /**
     * 创建客户端配置
     *
     * @param mongodbConfig
     * @return
     */
    private static MongoClientOptions buildMongoClientOptions(MongodbConfig mongodbConfig) {
        int timeOut = mongodbConfig.getTimeOut();
        MongoClientOptions mongoClientOptions = MongoClientOptions.builder().socketTimeout(timeOut)
                .connectTimeout(timeOut).serverSelectionTimeout(timeOut)
                .connectionsPerHost(mongodbConfig.getConnectionsPerHost()).build();
        return mongoClientOptions;
    }

    /**
     * 创建mongodb访问的服务器列表集合
     *
     * @param mongodbConfig
     * @return
     */
    private static List<ServerAddress> buildServerAddress(MongodbConfig mongodbConfig) {
        String[] hosts = mongodbConfig.getHost();
        List<ServerAddress> serverAddressList = new ArrayList<ServerAddress>();
        if (hosts != null) {
            for (int i = 0; i < hosts.length; i++) {
                String[] hp = hosts[i].split(":");
                ServerAddress serverAddress = null;
                if (hp == null) {
                    serverAddress = new ServerAddress("127.0.0.1", 27017);
                } else if (hp.length == 1) {
                    serverAddress = new ServerAddress(hp[0], 27017);
                } else if (hp.length > 1) {
                    serverAddress = new ServerAddress(hp[0], Integer.parseInt(hp[1]));
                }
                if (serverAddress != null) {
                    serverAddressList.add(serverAddress);
                }
            }
        }
        return serverAddressList;
    }

}
