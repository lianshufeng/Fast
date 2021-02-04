package com.fast.dev.robot.robotserver.core.factory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputSource {


    //保证名字与资源一定是对应的，用于缓存
    private String resourceName;

    //模板输入源
    private InputStream templateInputStream;


}
