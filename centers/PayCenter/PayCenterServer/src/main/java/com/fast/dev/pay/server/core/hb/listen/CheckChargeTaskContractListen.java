package com.fast.dev.pay.server.core.hb.listen;

import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAutoChargeContractDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseTaskDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseTaskJournalDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTaskJournal;
import com.fast.dev.pay.server.core.hb.event.CheckChargeTaskContractEvent;
import com.fast.dev.pay.server.core.hb.model.ChargeContractModel;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseCheckChargeTaskParameter;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeContractService;
import com.fast.dev.pay.server.core.hb.type.ChargeState;
import com.fast.dev.pay.server.core.hb.type.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckChargeTaskContractListen implements ApplicationListener<CheckChargeTaskContractEvent> {



    @Autowired
    private HuaXiaEnterpriseAutoChargeContractDao huaXiaEnterpriseAutoChargeContractDao;

    @Autowired
    private HuaXiaEnterpriseTaskDao huaXiaEnterpriseTaskDao;

    @Autowired
    private HuaXiaEnterpriseTaskJournalDao huaXiaEnterpriseTaskJournalDao;


    @Override
    public void onApplicationEvent(CheckChargeTaskContractEvent event) {
        HuaXiaEnterpriseTask task = event.getTask();
        log.info("onApplicationEvent : {} -> {} ", event.isSuccess(), task.getId());


        //取出参数
        HuaXiaEnterpriseCheckChargeTaskParameter parameter = task.readParameter(TaskType.CheckCharge);

        //取出扣款的流水
        HuaXiaEnterpriseTaskJournal journal = this.huaXiaEnterpriseTaskJournalDao.findTop1ById(parameter.getTaskJournalId());

        //取出扣款流水
        HuaXiaEnterpriseTask chargeTask = this.huaXiaEnterpriseTaskDao.findTop1ById(parameter.getChargeTaskId());

        //最后一次扣款信息
        ChargeContractModel contractModel = new ChargeContractModel();
        contractModel.setState(event.isSuccess() ? ChargeState.Success : ChargeState.Fail);
        contractModel.setTime(task.getLastExecuteTime());

        this.huaXiaEnterpriseAutoChargeContractDao.updateLastChargeContract(chargeTask.getContract().getId(), contractModel);
    }
}
