package com.fast.dev.ucenter.service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class TestController {

    @Value("${spring.thymeleaf.cache}")
    private String cache ;


    @RequestMapping("test")
    public String test(){
        return cache;
    }

}
