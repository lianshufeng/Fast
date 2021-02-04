package com.fast.dev.mq.mqserver.boot;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan("com.fast.dev.mq.mqserver.core")
@Import(MVCConfiguration.class)
@EnableApplicationClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, KafkaMetricsAutoConfiguration.class})
public class MQServerApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        SpringApplication.run(MQServerApplication.class, args);
    }

}
