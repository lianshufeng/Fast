package com.fast.dev.pay.server.core.hb.service;

import com.fast.dev.core.util.stream.StreamUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.server.core.hb.conf.HuaxiaConf;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseTaskDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseTaskJournalDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTaskJournal;
import com.fast.dev.pay.server.core.hb.event.*;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseChargeTaskParameter;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseCheckChargeTaskParameter;
import com.fast.dev.pay.server.core.hb.model.task.HuaXiaEnterpriseCheckFinishTaskParameter;
import com.fast.dev.pay.server.core.hb.type.JournalState;
import com.fast.dev.pay.server.core.hb.type.TaskState;
import com.fast.dev.pay.server.core.hb.type.TaskType;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务流程
 */
@Slf4j
@Service
public class HuaXiaEnterpriseTaskProcessService {

    @Autowired
    private HuaxiaConf huaxiaConf;

    @Autowired
    private HuaXiaEnterpriseTaskDao huaXiaEnterpriseTaskDao;

    @Autowired
    private HuaXiaEnterpriseTaskJournalDao huaXiaEnterpriseTaskJournalDao;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private HuaXiaApiService huaXiaApiService;

    @Autowired
    private HuaXiaEnterpriseTaskManagerService huaXiaEnterpriseTaskManagerService;


    @Autowired
    private ApplicationContext applicationContext;


    /**
     * 检查是否完成的任务
     *
     * @param task
     */
    @Transactional
    public void checkFinishTask(HuaXiaEnterpriseTask task) {
        log.info("checkFinishTask : {}", task.getId());
        HuaXiaEnterpriseCheckFinishTaskParameter parameter = task.readParameter(task.getType());

        //取出已完成的任务
        List<HuaXiaEnterpriseTask> finishTasks = StreamUtil.iterableToStream(this.huaXiaEnterpriseTaskDao.findAllById(parameter.getTaskIds()))
                .filter((it) -> {
                    return it.getState() == TaskState.Finish;
                }).collect(Collectors.toList());

        if (parameter.getTaskIds().size() != finishTasks.size()) {
            this.applicationContext.publishEvent(new CheckFinishTaskContractEvent(task).success(false));
            return;
        }

        log.info("扣款合同完成 : {}", task);

        //更新任务状态
        this.huaXiaEnterpriseTaskDao.updateTaskStateFinish(task.getId());


        //同步事件
        this.applicationContext.publishEvent(new CheckFinishTaskContractEvent(task).success(true));
    }

    /**
     * 扣款任务
     *
     * @param task
     */
    @Transactional
    public void chargeTask(HuaXiaEnterpriseTask task) {
        log.info("chargeTask : {}", task.getId());

        //当前任务是否存在检查扣款的任务，存在则不创建
        if (this.huaXiaEnterpriseTaskDao.existsByContractAndTypeAndParent(task.getContract(), TaskType.CheckCharge, task)) {
            log.info("检查扣款任务没有执行完成，不执行本次扣款任务  ： {} ", task.getId());
            return;
        }

        //任务执行的流水
        HuaXiaEnterpriseTaskJournal taskJournal = buildTaskJournal(task);

        //计算应该扣款金额
        HuaXiaEnterpriseChargeTaskParameter parameter = task.readParameter(task.getType());
        long amount = parameter.getTotalAmount() - parameter.getPaymentAmount();
        if (amount > huaxiaConf.getDayLimitAmount()) {
            amount = huaxiaConf.getDayLimitAmount();
        }

        //更新流水
        Map<String, Object> parm = taskJournal.getItems();

        //todo 华夏扣款
        HuaXiaApiService.ApiResult apiResult = this.huaXiaApiService.chargeTask(task, taskJournal, amount);
        if (apiResult.isSuccess() == false) {
            parm.put("amount", 0);
            updateTaskJournalFail(ChargeTaskContractEvent.class, task, taskJournal, apiResult.getFailReason());
            return;
        }



        parm.put("amount", amount);
        updateTaskJournalSuccess(ChargeTaskContractEvent.class, task, taskJournal, parm);


        //创建一个检查扣款是否成功的任务
        this.huaXiaEnterpriseTaskManagerService.addCheckChargeTask(task, taskJournal);


    }


    /**
     * 检查是否已扣款
     *
     * @param task
     */
    @Transactional
    @Synchronized
    public void checkChargeTask(HuaXiaEnterpriseTask task) {
        log.info("checkChargeTask : {}", task.getId());

//        取出任务参数
        HuaXiaEnterpriseCheckChargeTaskParameter parameter = task.readParameter(TaskType.CheckCharge);

        //取出扣款的流水
        HuaXiaEnterpriseTaskJournal journal = this.huaXiaEnterpriseTaskJournalDao.findTop1ById(parameter.getTaskJournalId());
        //取出扣款流水
        HuaXiaEnterpriseTask chargeTask = this.huaXiaEnterpriseTaskDao.findTop1ById(parameter.getChargeTaskId());


        //验证扣款金额
        final long amount = Long.valueOf(String.valueOf(journal.getItems().get("amount")));

        //TODO 调用华夏api，检查扣款是否成功
        HuaXiaApiService.ApiResult apiResult = this.huaXiaApiService.checkChargeTask(task);
        if (apiResult.isSuccess() == false) {

            //任务超时则自动删除本任务并且不执行后面的流程
            if (task.getLastExecuteTime() - task.getCreateTime() > huaxiaConf.getUserConfirmChargeTime()) {
                this.huaXiaEnterpriseTaskDao.removeById(task.getId());
                updateTaskJournalFail(CheckChargeTaskContractEvent.class, task, journal, "用户确认扣费超时");
                return;
            }

            updateTaskJournalFail(CheckChargeTaskContractEvent.class, task, journal, apiResult.getFailReason());
            return;
        }



        //更新扣款任务
        HuaXiaEnterpriseChargeTaskParameter chargeTaskParameter = chargeTask.readParameter(TaskType.Charge);
        chargeTaskParameter.setPaymentAmount(chargeTaskParameter.getPaymentAmount() + amount);
        this.huaXiaEnterpriseTaskDao.updateParameter(chargeTask.getId(), chargeTaskParameter);


        //更新任务状态,则表明扣款任务完成
        if (chargeTaskParameter.getPaymentAmount() >= chargeTaskParameter.getTotalAmount()) {
            this.huaXiaEnterpriseTaskDao.updateTaskStateFinish(chargeTask.getId());
        }

        //更新扣款流水状态为完成
        journal.setState(JournalState.Success);

        //删除此检查扣款的任务
        this.huaXiaEnterpriseTaskDao.removeById(task.getId());

        updateTaskJournalSuccess(CheckChargeTaskContractEvent.class, task, journal, null);
    }


    /**
     * 冻结任务
     *
     * @param task
     */
    @Transactional
    public void freezeTask(HuaXiaEnterpriseTask task) {
        log.info("freezeTask {}", task.getId());
        //任务执行的流水
        HuaXiaEnterpriseTaskJournal taskJournal = buildTaskJournal(task);

        //todo 华夏冻结金额
        HuaXiaApiService.ApiResult apiResult = this.huaXiaApiService.freezeTask(task);
        if (apiResult.isSuccess() == false) {
            updateTaskJournalFail(FreezeTaskContractEvent.class, task, taskJournal, apiResult.getFailReason());
            return;
        }


        //修改任务状态为完成
        this.huaXiaEnterpriseTaskDao.updateTaskStateFinish(task.getId());


        //创建扣款任务
        List<HuaXiaEnterpriseTask> chargeTasks = this.huaXiaEnterpriseTaskManagerService.addChargeTask(task.getContract(), task);
        //检查是否所有任务都检查完的任务
        this.huaXiaEnterpriseTaskManagerService.addCheckFinishTask(chargeTasks, task);


        //更新同步流水
        updateTaskJournalSuccess(FreezeTaskContractEvent.class, task, taskJournal, task.getParameter());
    }


    /**
     * 更新任务流水：成功
     *
     * @param cls
     * @param task
     * @param taskJournal
     * @param items
     */
    @SneakyThrows
    private void updateTaskJournalSuccess(Class<? extends SuperContractEvent> cls, HuaXiaEnterpriseTask task, HuaXiaEnterpriseTaskJournal taskJournal, Map<String, Object> items) {
        //更新流水
        taskJournal.setState(JournalState.Success);
        taskJournal.setItems(items);
        updateTaskJournal(taskJournal);

        //同步事件
        SuperContractEvent superContractEvent = (SuperContractEvent) cls.getConstructors()[0].newInstance(task);
        this.applicationContext.publishEvent(superContractEvent.success(true));
    }

    /**
     * 更新任务流水：失败
     *
     * @param cls
     * @param task
     * @param taskJournal
     * @param failReason
     */
    @SneakyThrows
    private void updateTaskJournalFail(Class<? extends SuperContractEvent> cls, HuaXiaEnterpriseTask task, HuaXiaEnterpriseTaskJournal taskJournal, String failReason) {

        //更新流水
        taskJournal.setState(JournalState.Fail);
        taskJournal.setFailreason(failReason);
        updateTaskJournal(taskJournal);

        //同步事件
        SuperContractEvent superContractEvent = (SuperContractEvent) cls.getConstructors()[0].newInstance(task);
        this.applicationContext.publishEvent(superContractEvent.success(false));

    }


    /**
     * 创建任务流水
     *
     * @return
     */
    private HuaXiaEnterpriseTaskJournal buildTaskJournal(HuaXiaEnterpriseTask task) {
        HuaXiaEnterpriseTaskJournal taskJournal = this.huaXiaEnterpriseTaskJournalDao.buildTaskJournal();
        taskJournal.setState(JournalState.Wait);
        taskJournal.setHuaXiaEnterpriseTask(task);
        taskJournal.setContract(task.getContract());
        taskJournal.setItems(new HashMap<>());
        taskJournal.setExecuteTime(this.dbHelper.getTime());
        this.huaXiaEnterpriseTaskJournalDao.updateTaskJournal(taskJournal);
        return taskJournal;
    }


    /**
     * 更新任务流水
     *
     * @param taskJournal
     */
    private void updateTaskJournal(HuaXiaEnterpriseTaskJournal taskJournal) {
        this.huaXiaEnterpriseTaskJournalDao.updateTaskJournal(taskJournal);
    }


}
