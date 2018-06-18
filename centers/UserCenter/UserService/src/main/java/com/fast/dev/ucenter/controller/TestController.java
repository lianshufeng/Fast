package com.fast.dev.ucenter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${spring.thymeleaf.cache}")
    private String cache ;


    @RequestMapping("test")
    public String test(){
        return cache;
    }

}
