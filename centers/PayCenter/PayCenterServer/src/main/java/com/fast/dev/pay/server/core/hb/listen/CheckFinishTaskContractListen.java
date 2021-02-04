package com.fast.dev.pay.server.core.hb.listen;

import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.event.CheckFinishTaskContractEvent;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeContractService;
import com.fast.dev.pay.server.core.hb.type.ContractState;
import groovy.transform.AutoImplement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckFinishTaskContractListen implements ApplicationListener<CheckFinishTaskContractEvent> {

    @Autowired
    private HuaXiaEnterpriseAutoChargeContractService huaXiaEnterpriseAutoChargeContractService;

    @Override
    public void onApplicationEvent(CheckFinishTaskContractEvent event) {
        HuaXiaEnterpriseTask task = event.getTask();
        log.info("onApplicationEvent : {} -> {} ", event.isSuccess(), task.getId());


        //修改合同状态为完成
        if (event.isSuccess()) {
            String contractId = task.getContract().getId();
            huaXiaEnterpriseAutoChargeContractService.updateState(contractId, ContractState.Finish);
        }


    }
}
