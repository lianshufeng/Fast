package com.fast.dev.ucenter.core.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "kaptcha")
public class KaptchaConf {


    /**
     * 宽度
     */
    private int width = 100;
    /**
     * 高度
     */
    private int height = 41;
    /**
     * 字体颜色
     */
    private String fontColor = "blue";
    /**
     * 字体名
     */
    private String fontNames = "宋体,楷体,微软雅黑";


}
