package com.fast.dev.components.core.config;

import com.fast.dev.components.core.conf.SwaggerConfig;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Log
@EnableSwagger2
@Configuration
@ComponentScan("com.fast.dev.components.core")
public class SwaggerConfiguration {

    @Value("${server.port}")
    private int port;

    @Autowired
    private SwaggerConfig swaggerConfig;


    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swaggerConfig.build())
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerConfig.getPackageName()))
                .paths(PathSelectors.any())
                .build();
    }


    @Autowired
    private void init() {
        String localhost = "http://localhost:" + port;

        log.info("DocApi UI : " + (localhost + "/swagger-ui.html"));
        log.info("PostMain Import : " + (localhost + "/v2/api-docs"));

    }


}
