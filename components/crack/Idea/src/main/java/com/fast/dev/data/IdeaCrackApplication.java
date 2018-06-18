package com.fast.dev.data;

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
public class IdeaCrackApplication extends ApplicationBootSuper {

    private final static Logger logger = LoggerFactory.getLogger(IdeaCrackApplication.class);

    @Value("${server.port}")
    private int serverPort;

    public static void main(String[] args) {
        SpringApplication.run(IdeaCrackApplication.class, args);
    }

    @PostConstruct
    private void printActiveUrl() {
        logger.info("----------------------↓↓↓↓↓↓↓↓↓↓↓↓");
        logger.info("Jrebel\t online active : http://127.0.0.1:" + this.serverPort + "/" + UUID.randomUUID().toString());
        logger.info("Idea\t online active : http://127.0.0.1:" + this.serverPort + "/");
        logger.info("----------------------↑↑↑↑↑↑↑↑↑↑↑↑");
    }


}
