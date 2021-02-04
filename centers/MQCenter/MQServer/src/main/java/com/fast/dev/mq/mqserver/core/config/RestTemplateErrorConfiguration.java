package com.fast.dev.mq.mqserver.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Configuration
public class RestTemplateErrorConfiguration {

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private void init() {
        restTemplate.setErrorHandler(responseErrorHandler());
    }


    @Bean
    public ResponseErrorHandler responseErrorHandler() {
        return new ResponseErrorHandler() {

            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                if (response.getStatusCode().value() != 200) {
                    log.error(String.format("response code : %s error :%s ", response.getStatusCode().value(), response.getStatusText()));
                }
                return true;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

            }
        };
    }


}
