package com.fast.dev.pay.server.core.hb.listen;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.event.FreezeTaskContractEvent;
import com.fast.dev.pay.server.core.hb.model.ContractFreezeModel;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeContractService;
import com.fast.dev.pay.server.core.hb.type.ContractFreezeState;
import com.fast.dev.pay.server.core.hb.type.ContractState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FreezeTaskContractListen implements ApplicationListener<FreezeTaskContractEvent> {
    @Autowired
    private HuaXiaEnterpriseAutoChargeContractService huaXiaEnterpriseAutoChargeContractService;

    @Autowired
    private DBHelper dbHelper;

    @Override
    public void onApplicationEvent(FreezeTaskContractEvent event) {
        HuaXiaEnterpriseTask task = event.getTask();
        log.info("onApplicationEvent : {} -> {} ", event.isSuccess(), task.getId());

        String contractId = task.getContract().getId();
        //冻结成功
        if (event.isSuccess()) {
            this.huaXiaEnterpriseAutoChargeContractService.updateFreeze(contractId, new ContractFreezeModel(this.dbHelper.getTime(), ContractFreezeState.Finish));
            this.huaXiaEnterpriseAutoChargeContractService.updateState(contractId, ContractState.WaitCharge);
            return;
        }

        this.huaXiaEnterpriseAutoChargeContractService.updateFreeze(contractId, new ContractFreezeModel(this.dbHelper.getTime(), ContractFreezeState.Fail));



    }
}
