package com.fast.dev.component.remotelock.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 远程锁配置对象
 *
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2018年1月17日
 */

@Component
@ConfigurationProperties(prefix = "fast.remotelock")

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LockOption {

    // 会话超时时间
    private int sessionTimeout = 10000;

    //主机连接字符串
    private String hostConnectString;

    //区分的命名空间
    private String nameSpace = "_default_";


}
