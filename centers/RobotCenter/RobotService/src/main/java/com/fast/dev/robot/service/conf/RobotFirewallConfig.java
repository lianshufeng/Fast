package com.fast.dev.robot.service.conf;

import com.fast.dev.robot.service.type.RobotType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 机器防御的配置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "robot")
public class RobotFirewallConfig {

    //令牌超时时间
    private long tokenTimeOut = 1000 * 60 * 5;

    //机器人业务模块的名称
    private String robotServerName = "robotserver";

    //容错率
    private float errorRate = 0.01f;

    //特别的描述
    private Map<String, RobotFirewallConfig> other = new HashMap();


    /**
     * 获取指定类型的配置
     *
     * @param robotType
     * @return
     */
    public RobotFirewallConfig get(RobotType robotType) {
        RobotFirewallConfig setRobotFirewallConfig = this.other.get(robotType.name());

        RobotFirewallConfig ret = new RobotFirewallConfig();
        //如果没有配置则取默认值
        if (setRobotFirewallConfig == null) {
            BeanUtils.copyProperties(this, ret);
        } else {
            //覆盖默认配置
            BeanUtils.copyProperties(this, ret);
            //覆盖用户配置
            BeanUtils.copyProperties(setRobotFirewallConfig, ret);
        }
        return ret;
    }

}
