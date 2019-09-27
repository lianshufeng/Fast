package com.fast.components.robotfirewall.helper;

import com.fast.components.robotfirewall.service.RobotValidateService;
import com.fast.components.robotfirewall.type.RobotType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 机器人防火墙助手
 */
@Component
public class RobotFirewallHelper {

    @Autowired
    private ApplicationContext applicationContext;

    private Map<RobotType, RobotValidateService> robotMap = new ConcurrentHashMap<>();

    @Autowired
    private void init() {
        this.applicationContext.getBeansOfType(RobotValidateService.class).values().forEach((it) -> {
            robotMap.put(it.robotType(), it);
        });
    }

    /**
     * 创建验证码
     *
     * @param robotType
     * @return
     */
    public RobotValidateService get(RobotType robotType) {
        return this.robotMap.get(robotType);
    }


}
