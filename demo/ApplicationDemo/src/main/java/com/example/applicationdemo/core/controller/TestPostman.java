package com.example.applicationdemo.core.controller;

import com.example.applicationdemo.core.model.TestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 作者：练书锋
 * 时间：2018/8/24
 */
@Slf4j
// 支持动态刷新配置
@RefreshScope
@RestController
public class TestPostman {


    /**
     * 测试
     *
     * @param test  这个是测试1的参数
     * @param info  信息参数
     * @param test2 这个是测试2的参数
     * @return
     */
    @RequestMapping("link1")
    public Object test(TestModel test, String info, String test2) {
        return test;
    }


    /**
     * 测试2
     *
     * @return
     */
    @RequestMapping("link2")
    public Object test2(String uid, HttpServletRequest request) {
        return uid;
    }

}
