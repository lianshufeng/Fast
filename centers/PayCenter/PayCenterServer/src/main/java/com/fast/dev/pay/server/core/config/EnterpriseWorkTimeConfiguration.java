package com.fast.dev.pay.server.core.config;

import com.fast.dev.data.timer.config.TaskTimerConfiguration;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.scheduled.EnterpriseWorkScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnterpriseWorkTimeConfiguration extends TaskTimerConfiguration {

    @Autowired
    private EnterpriseWorkScheduler enterpriseWorkScheduler;

    @Override
    public TaskTimerItem[] register() {
        return new TaskTimerItem[]{
                TaskTimerItem.builder().taskTimerTable(HuaXiaEnterpriseAccount.class).taskTimerEvent(enterpriseWorkScheduler).build()
        };
    }
}
