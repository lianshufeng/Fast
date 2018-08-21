package com.fast.dev.ucenter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 * 设备信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfo {

    /**
     * 操作系统
     */
    private String os;


    /**
     * 设备的唯一标识 ( 手机为设备id或浏览器指纹)
     */
    private String uuid;

    /**
     * 分辨率宽度
     */
    private Integer width;

    /**
     * 分辨率高度
     */
    private Integer height;

    /**
     * 客户端信息
     */
    private Map<String, Object> other;


}
