package com.fast.dev.ucenter.security.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 用户缓存
 *
 * @作者 练书锋
 * @联系 251708339@qq.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "tokencache")
public class UserTokenCacheConf implements Serializable {

    // 总共缓存的时间（单位秒）
    private long timeToLiveSeconds = 600;

    // 最后一次访问缓存的日期至失效之时的时间间隔（单位秒）
    private long timeToIdleSeconds = 300;

    // 最大的缓存对象的数量
    private int maxMemoryCount = 1000;

    // 内存不够是否自动写到磁盘上
    private boolean overflowToDisk = false;


}
