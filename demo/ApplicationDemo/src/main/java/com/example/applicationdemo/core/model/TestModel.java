package com.example.applicationdemo.core.model;

import lombok.Data;

import java.util.Map;

@Data
public class TestModel {
    //名称
    private String name;
    //年龄
    private int age;
    /**
     * 创建时间
     */
    private long createTime;
    private Boolean qiong;
    private Map<String, Object> map;
    private String[] flag;


    private TestModel test;


}