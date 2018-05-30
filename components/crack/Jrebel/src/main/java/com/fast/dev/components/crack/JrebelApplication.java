package com.fast.dev.components.crack;

import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.util.UUID;


@ComponentScan("com.fast.dev.components.crack")
@Import(MVCConfiguration.class)
public class JrebelApplication extends ApplicationBootSuper {

    private final static Logger logger = LoggerFactory.getLogger(JrebelApplication.class);

    @Value("${server.port}")
    private int serverPort;

    public static void main(String[] args) {
        SpringApplication.run(JrebelApplication.class, args);
    }

    @PostConstruct
    private void init(){
        logger.info("----------------------↓↓↓↓↓↓↓↓↓↓↓↓");
        logger.info("online active : http://127.0.0.1:"+serverPort+"/"+UUID.randomUUID().toString());
        logger.info("----------------------↑↑↑↑↑↑↑↑↑↑↑↑");
    }


}
