package com.fast.dev.pay.server.core.hb.aop.hbapi;

import com.fast.dev.core.event.method.MethodEvent;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaApiJournalDao;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HuaXiaEnterpriseHuaXiaApiServiceHookListen implements ApplicationListener<HuaXiaEnterpriseHuaXiaApiServiceHookEvent> {


    @Autowired
    private HuaXiaEnterpriseHuaXiaApiServiceHelper huaXiaEnterpriseHuaXiaApiServiceHelper;


    @Override
    @SneakyThrows
    public void onApplicationEvent(HuaXiaEnterpriseHuaXiaApiServiceHookEvent event) {
        MethodEvent.Source source = event.getSource();
        if (source.getCallType() == MethodEvent.CallType.Before) {
            this.huaXiaEnterpriseHuaXiaApiServiceHelper.build(source.getMethodName());
            log.info("创建Api流水 : {} -> {} ", this.huaXiaEnterpriseHuaXiaApiServiceHelper.getTradeNo(), source.getMethodName());
        } else if (source.getCallType() == MethodEvent.CallType.After) {
            if (source.getThrowable() == null) {
                log.info("暂存Api流水 :{} ", this.huaXiaEnterpriseHuaXiaApiServiceHelper.getTradeNo());
                this.huaXiaEnterpriseHuaXiaApiServiceHelper.finish();
            } else {
                log.error("保留Api流水 , 异常 : {}", source.getThrowable().getMessage());
            }


        }
    }


}
