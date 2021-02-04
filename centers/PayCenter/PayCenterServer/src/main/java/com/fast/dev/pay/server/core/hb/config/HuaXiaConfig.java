package com.fast.dev.pay.server.core.hb.config;

import com.fast.dev.pay.server.core.hb.conf.HuaxiaConf;
import com.fast.dev.pay.server.core.hb.service.HuaXiaApiService;
import com.fast.dev.pay.server.core.hb.service.impl.HuaXiaApiServiceImpl;
import com.fast.dev.pay.server.core.hb.service.impl.HuaXiaApiServiceImplExecuteSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HuaXiaConfig {

    @Autowired
    private HuaxiaConf huaxiaConf;

    @Bean
    public HuaXiaApiService huaXiaApiService() {
        return huaxiaConf.isUseHuaXiaApi() ? new HuaXiaApiServiceImpl() : new HuaXiaApiServiceImplExecuteSuccess();
    }


}
