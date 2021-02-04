package com.fast.dev.pay.server.core.hb.listen;

import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAutoChargeContractDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseTaskDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseTaskJournalDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.event.ChargeTaskContractEvent;
import com.fast.dev.pay.server.core.hb.model.ChargeContractModel;
import com.fast.dev.pay.server.core.hb.type.ChargeState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChargeTaskContractListen implements ApplicationListener<ChargeTaskContractEvent> {


    @Autowired
    private HuaXiaEnterpriseAutoChargeContractDao huaXiaEnterpriseAutoChargeContractDao;

    @Autowired
    private HuaXiaEnterpriseTaskDao huaXiaEnterpriseTaskDao;

    @Autowired
    private HuaXiaEnterpriseTaskJournalDao huaXiaEnterpriseTaskJournalDao;


    @Override
    public void onApplicationEvent(ChargeTaskContractEvent event) {
        HuaXiaEnterpriseTask task = event.getTask();
        log.info("onApplicationEvent : {} -> {} ", event.isSuccess(), task.getId());

        ChargeContractModel contractModel = new ChargeContractModel();
        contractModel.setTime(task.getLastExecuteTime());

        //如果扣款成功则
        contractModel.setState(event.isSuccess() ? ChargeState.WaitCheck : ChargeState.Fail);

        //同步扣款信息
        this.huaXiaEnterpriseAutoChargeContractDao.updateLastChargeContract(task.getContract().getId(), contractModel);

    }
}
