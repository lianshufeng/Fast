package com.fast.dev.pay.server.core.hb.service;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAutoChargeContractDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseTaskDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeContract;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTaskJournal;
import com.fast.dev.pay.server.core.hb.helper.TimeHelper;
import com.fast.dev.pay.server.core.hb.model.AutoChargeInfo;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseChargeTaskParameter;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseCheckChargeTaskParameter;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseCheckFinishTaskParameter;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseFreezeTaskParameter;
import com.fast.dev.pay.server.core.hb.service.task.HuaXiaTaskExecuteManager;
import com.fast.dev.pay.server.core.hb.type.TaskType;
import com.fast.dev.pay.server.core.util.AutoChargeInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HuaXiaEnterpriseTaskManagerService {

    @Autowired
    private HuaXiaEnterpriseTaskDao huaXiaEnterpriseTaskDao;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private HuaXiaTaskExecuteManager huaXiaTaskExecuteManager;

    @Autowired
    private HuaXiaEnterpriseAutoChargeContractDao huaXiaEnterpriseAutoChargeContractDao;

    //调度器
    private Timer timer = new Timer();


    /**
     * 添加一个冻结任务
     *
     * @param contract
     */
    @Transactional
    public HuaXiaEnterpriseTask addFreezeTask(HuaXiaEnterpriseAutoChargeContract contract) {
        HuaXiaEnterpriseFreezeTaskParameter parameter = new HuaXiaEnterpriseFreezeTaskParameter();
        parameter.setAmount(AutoChargeInfoUtil.countAmount(contract.getAutoCharge().getAutoChargeInfos()));
        //构建任务参数
        HuaXiaEnterpriseTask task = new HuaXiaEnterpriseTask();
        //生效时间
        task.setEffectTime(this.dbHelper.getTime());
        task.setType(TaskType.Freeze);
        task.setContract(contract);
        task.writeParameter(parameter);
        task.setHuaXiaEnterpriseAccount(contract.getHuaXiaEnterpriseAccount());

        this.huaXiaEnterpriseTaskDao.addTask(task);
        return task;
    }


    /**
     * 创建扣款任务
     *
     * @param contract
     * @return
     */
    @Transactional
    public List<HuaXiaEnterpriseTask> addChargeTask(HuaXiaEnterpriseAutoChargeContract contract, HuaXiaEnterpriseTask parentTask) {
        List<HuaXiaEnterpriseTask> tasks = new ArrayList<>();
        for (AutoChargeInfo chargeInfo : contract.getAutoCharge().getAutoChargeInfos()) {

            HuaXiaEnterpriseChargeTaskParameter parameter = new HuaXiaEnterpriseChargeTaskParameter();
            parameter.setTime(chargeInfo.getTime());
            parameter.setTotalAmount(chargeInfo.getAmount());
            parameter.setPaymentAmount(0);

            //构建任务参数
            HuaXiaEnterpriseTask task = new HuaXiaEnterpriseTask();
            long effectTime = Long.valueOf(chargeInfo.getTime()) * 1000 * 60 * 60 * 24;
            //生效时间
            task.setEffectTime(this.dbHelper.getTime() + effectTime);
            task.setType(TaskType.Charge);
            task.setContract(contract);
            task.writeParameter(parameter);
            task.setHuaXiaEnterpriseAccount(contract.getHuaXiaEnterpriseAccount());
            task.setParent(parentTask);

            this.huaXiaEnterpriseTaskDao.addTask(task);
            chargeInfo.setTaskId(task.getId());
            tasks.add(task);
        }
        //更新任务id到合同里
        this.huaXiaEnterpriseAutoChargeContractDao.updateAutoCharge(contract.getId(), contract.getAutoCharge());

        return tasks;
    }


    /**
     * 检查是否扣款成功的任务
     *
     * @return
     */
    @Transactional
    public HuaXiaEnterpriseTask addCheckChargeTask(HuaXiaEnterpriseTask chargeTask,  HuaXiaEnterpriseTaskJournal taskJournal) {
        //构建任务参数
        HuaXiaEnterpriseTask task = new HuaXiaEnterpriseTask();
        //生效时间
        task.setEffectTime(this.dbHelper.getTime());
        task.setType(TaskType.CheckCharge);
        task.setContract(chargeTask.getContract());

        //构建参数
        HuaXiaEnterpriseCheckChargeTaskParameter parameter = new HuaXiaEnterpriseCheckChargeTaskParameter();
        parameter.setChargeTaskId(chargeTask.getId());
        parameter.setTaskJournalId(taskJournal.getId());

        task.writeParameter(parameter);
        task.setHuaXiaEnterpriseAccount(chargeTask.getContract().getHuaXiaEnterpriseAccount());
        task.setParent(chargeTask);

        this.huaXiaEnterpriseTaskDao.addTask(task);

        return task;
    }


    /**
     * 添加检查是否所有的扣款都已完成的任务
     *
     * @return
     */
    @Transactional
    public HuaXiaEnterpriseTask addCheckFinishTask(List<HuaXiaEnterpriseTask> chargeTask, HuaXiaEnterpriseTask parentTask) {

        HuaXiaEnterpriseCheckFinishTaskParameter parameter = new HuaXiaEnterpriseCheckFinishTaskParameter();
        parameter.setTaskIds(chargeTask.stream().filter((it) -> {
            return it.getType() == TaskType.Charge;
        }).map((it) -> {
            return it.getId();
        }).collect(Collectors.toSet()));

        //合同
        HuaXiaEnterpriseAutoChargeContract contract = chargeTask.get(0).getContract();

        //构建任务参数
        HuaXiaEnterpriseTask task = new HuaXiaEnterpriseTask();
        //生效时间
        task.setEffectTime(this.dbHelper.getTime());
        task.setType(TaskType.CheckFinish);
        task.setContract(contract);
        task.writeParameter(parameter);
        task.setHuaXiaEnterpriseAccount(contract.getHuaXiaEnterpriseAccount());
        task.setParent(parentTask);

        this.huaXiaEnterpriseTaskDao.addTask(task);

        return task;
    }


    /**
     * 触发企业执行任务
     */
    public void executeEnterpriseTask(HuaXiaEnterpriseAccount... huaXiaEnterpriseAccount) {
        for (HuaXiaEnterpriseAccount account : huaXiaEnterpriseAccount) {
            executeEnterpriseTask(account, new int[]{0, 3, 37});
        }
    }


    /**
     * 补偿执行企业任务
     *
     * @param account
     */
    private void executeEnterpriseTask(final HuaXiaEnterpriseAccount account, int[] sleepCount) {
        for (int sleep : sleepCount) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runThreadExecuteTask(account);
                }
            }, 1000 * 60 * sleep);
        }
    }


    /**
     * 使用线程执行企业任务
     *
     * @param account
     */
    private void runThreadExecuteTask(HuaXiaEnterpriseAccount account) {
        huaXiaTaskExecuteManager.execute(() -> {
            try {
                executeEnterpriseTask(account);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        });
    }


    /**
     * 延迟重复执行任务
     */
    private void executeEnterpriseTask(HuaXiaEnterpriseAccount account) {
        log.info("executeEnterpriseTask : {} ", account);

        //处理冻结任务
        huaXiaTaskExecuteManager.executeTask(TaskType.Freeze, account, TimeHelper.TimeType.Minute);

        //处理扣款任务
        huaXiaTaskExecuteManager.executeTask(TaskType.Charge, account, TimeHelper.TimeType.Minute);

        //检查合同是否已完成
        huaXiaTaskExecuteManager.executeTask(TaskType.CheckFinish, account, TimeHelper.TimeType.Minute);
    }


}
