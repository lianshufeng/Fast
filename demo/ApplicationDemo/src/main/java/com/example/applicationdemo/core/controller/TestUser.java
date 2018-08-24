package com.example.applicationdemo.core.controller;

import com.fast.dev.core.util.result.InvokerResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者：练书锋
 * 时间：2018/8/24
 */
@RestController
public class TestUser {

    @RequestMapping("ping")
    public Object ping() {
        return InvokerResult.success("11");
    }


}
