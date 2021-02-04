package com.fast.dev.pay.server.core.hb.service.task;

import com.fast.dev.core.util.iterator.IteratorUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseTaskDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.helper.TimeHelper;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseTaskProcessService;
import com.fast.dev.pay.server.core.hb.type.TaskType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 华夏任务执行器
 */
@Slf4j
public abstract class HuaXiaTaskExecuteService {


    //最大获取任务数量
    private static final int GetMaxTaskCount = 10;

    @Autowired
    protected DBHelper dbHelper;

    @Autowired
    protected TimeHelper timeHelper;

    @Autowired
    protected HuaXiaEnterpriseTaskDao huaXiaEnterpriseTaskDao;

    @Autowired
    protected HuaXiaEnterpriseTaskProcessService processService;


    @Autowired
    private HuaXiaTaskExecuteManager huaXiaTaskExecuteManager;


    /**
     * 任务类型
     *
     * @return
     */
    public abstract TaskType taskType();


    /**
     * 执行任务
     *
     * @param account
     */
    public void execute(HuaXiaEnterpriseAccount account, TimeHelper.TimeType type) {

        //现在时间
        long effectTime = dbHelper.getTime();

        //取不能执行任务的时间段
        long[] workTime = this.timeHelper.getTime(type);

        IteratorUtil.execute(() -> {
            return this.huaXiaEnterpriseTaskDao.findTask(account, taskType(), workTime[0], workTime[1], effectTime, GetMaxTaskCount);
        }, (ret) -> {
            return ret.size() > 0;
        }, (ret) -> {
            ret.forEach((it) -> {
                huaXiaTaskExecuteManager.execute(() -> {
                    executeTask(it);
                });
            });
        });
    }

    /**
     * 执行任务
     *
     * @param task
     */
    @SneakyThrows
    private void executeTask(final HuaXiaEnterpriseTask task) {
        //开启新线程，解决事务问题
        try {
            execute(task);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 执行任务
     *
     * @param task
     */
    public abstract void execute(HuaXiaEnterpriseTask task);


}
