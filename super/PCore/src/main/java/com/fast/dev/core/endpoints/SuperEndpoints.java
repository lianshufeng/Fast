package com.fast.dev.core.endpoints;


import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 所有终端的父类
 */
@RequestMapping(SuperEndpoints.DefaultEndPointName)
public abstract class SuperEndpoints {

    /**
     * 默认的端点名称
     */
    public final static  String DefaultEndPointName = "endpoints";





}
