package com.fast.dev.pay.server.core.hb.aop.contract.listen;

import com.fast.dev.core.event.method.MethodEvent;
import com.fast.dev.data.token.service.ResourceTokenService;
import com.fast.dev.pay.server.core.hb.aop.contract.event.HuaXiaEnterpriseTaskProcessServiceEvent;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HuaXiaEnterpriseTaskProcessServiceListen implements ApplicationListener<HuaXiaEnterpriseTaskProcessServiceEvent> {

//    @Autowired
//    private RemoteLock remoteLock;

    @Autowired
    private ResourceTokenService resourceTokenService;

    private ThreadLocal<ResourceTokenService.Token> syncTokenThreadLocal = new ThreadLocal<>();

    @Override
    @SneakyThrows
    public void onApplicationEvent(HuaXiaEnterpriseTaskProcessServiceEvent event) {
        MethodEvent.Source source = event.getSource();
        //判断参数类型
        if (!(source.getParm()[0] instanceof HuaXiaEnterpriseTask)) {
            return;
        }
        HuaXiaEnterpriseTask task = (HuaXiaEnterpriseTask) source.getParm()[0];
        if (source.getCallType() == MethodEvent.CallType.Before) {
            log.info("锁定资源 : {} 类型 : {} 任务 : {} ", task.getContract().getId(), task.getType(), task.getId());
            syncTokenThreadLocal.set(resourceTokenService.token("paycenter_contract_" + task.getContract().getId()));
//            syncTokenThreadLocal.set(this.remoteLock.queue("paycenter_contract_" + task.getContract().getId()));
        } else if (source.getCallType() == MethodEvent.CallType.After) {
            log.info("解锁资源 : {}", task.getContract().getId());
            syncTokenThreadLocal.get().close();
            syncTokenThreadLocal.remove();
        }
    }


}
