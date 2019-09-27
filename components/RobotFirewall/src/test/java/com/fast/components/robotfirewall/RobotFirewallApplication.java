package com.fast.components.robotfirewall;

import com.fast.components.robotfirewall.config.RobotFirewallConfig;
import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;


@Import({MVCConfiguration.class, RobotFirewallConfig.class})
public class RobotFirewallApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        SpringApplication.run(RobotFirewallApplication.class, args);
    }


}
