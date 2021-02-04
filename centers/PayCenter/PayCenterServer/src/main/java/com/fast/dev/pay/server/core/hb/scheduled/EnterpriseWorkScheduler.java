package com.fast.dev.pay.server.core.hb.scheduled;

import com.fast.dev.core.util.iterator.IteratorUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.timer.domain.SimpleTaskTimerTable;
import com.fast.dev.data.timer.event.SimpleTaskTimerEvent;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAccountDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseTaskDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.helper.TimeHelper;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseTaskManagerService;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseTaskProcessService;
import com.fast.dev.pay.server.core.hb.service.task.HuaXiaTaskExecuteManager;
import com.fast.dev.pay.server.core.hb.type.TaskType;
import com.fast.dev.pay.server.core.hb.util.RandomUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@EnableScheduling
@Component
public class EnterpriseWorkScheduler implements SimpleTaskTimerEvent<HuaXiaEnterpriseAccount> {

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private HuaXiaEnterpriseAccountDao huaXiaEnterpriseAccountDao;

    @Autowired
    private HuaXiaEnterpriseTaskManagerService huaXiaEnterpriseTaskManagerService;

    @Autowired
    private HuaXiaEnterpriseTaskDao huaXiaEnterpriseTaskDao;

    @Autowired
    private TimeHelper timeFormatHelper;

    @Autowired
    private HuaXiaEnterpriseTaskProcessService huaXiaEnterpriseTaskProcessService;

    @Autowired
    private HuaXiaTaskExecuteManager huaXiaTaskExecuteManager;


    private final static int maxTaskCount = 5;

    private ExecutorService threadPool = Executors.newFixedThreadPool(5);


    @PreDestroy
    private void shutdown() {
        threadPool.shutdownNow();
    }


//    @Scheduled(cron = "0/10 * * * * ?")
//    @Scheduled(cron = "0/60 * * * * ?")
//    public void workTimer() {
//        threadPool.execute(() -> {
//            findWorkEnterprise();
//        });
//    }


    @Scheduled(cron = "0/60 * * * * ?")
    public void checkChargeTaskTimer() {
        threadPool.execute(() -> {
            checkChargeTask();
        });
    }


    /**
     * 检查扣款任务
     */
    @SneakyThrows
    private void checkChargeTask() {
        //随机延迟，将留给其他集群的服务器处理机会
        Thread.sleep(RandomUtil.getRandom(0, 3000));
        long[] nowTime = timeFormatHelper.getNowMinute();

        long time = this.dbHelper.getTime();
        //迭代器：功能,条件,输出
        IteratorUtil.execute(() -> {
            return huaXiaEnterpriseTaskDao.findTask(TaskType.CheckCharge, nowTime[0], nowTime[1], time, 5);
        }, (ret) -> {
            return ret.size() > 0;
        }, (tasks) -> {
            tasks.forEach((task) -> {
                huaXiaTaskExecuteManager.execute(() -> {
                    //todo 如果同时操作一个事务，mongo可能报错
                    huaXiaEnterpriseTaskProcessService.checkChargeTask(task);
                });
            });
        });

    }


//    /**
//     * 查询正在工作的企业
//     */
//    @SneakyThrows
//    private void findWorkEnterprise() {
//        //随机延迟，将留给其他集群的服务器处理机会
//        Thread.sleep(RandomUtil.getRandom(0, 3000));
//
//        final int workTime = timeFormatHelper.getNowWorkTime();
//        log.info("WorkTime : {}", workTime);
//
//        //迭代器：功能,条件,输出
//        IteratorUtil.execute(() -> {
//            return huaXiaEnterpriseAccountDao.findWorkEnterprise(maxTaskCount, workTime);
//        }, (ret) -> {
//            return ret.size() > 0;
//        }, (accountList) -> {
//            executeEnterpriseWorkTask(accountList);
//        });
//
//    }

//    /**
//     * 执行企业任务
//     *
//     * @param accountList
//     */
//    private void executeEnterpriseWorkTask(List<HuaXiaEnterpriseAccount> accountList) {
//        this.huaXiaEnterpriseTaskManagerService.executeEnterpriseTask(accountList.toArray(new HuaXiaEnterpriseAccount[0]));
//    }


    @Override
    public void execute(HuaXiaEnterpriseAccount account) {
        this.huaXiaEnterpriseTaskManagerService.executeEnterpriseTask(new HuaXiaEnterpriseAccount[]{account});
    }
}
