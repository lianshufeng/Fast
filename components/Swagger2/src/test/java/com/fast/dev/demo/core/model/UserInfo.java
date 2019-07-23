package com.fast.dev.demo.core.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户详情
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class UserInfo {

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String userName;


    /**
     * 年龄
     */
    @ApiModelProperty("年龄")
    private int age;


    /**
     * 查询时间
     */
    @ApiModelProperty("查询时间")
    private long queryTime;

}
