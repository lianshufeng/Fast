package com.fast.dev.pay.server.core.hb.dao.exnted;

import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseTaskParameter;
import com.fast.dev.pay.server.core.hb.type.TaskState;
import com.fast.dev.pay.server.core.hb.type.TaskType;

import java.util.List;

public interface HuaXiaEnterpriseTaskDaoExtend {


    /**
     * 增加任务
     */
    String addTask(HuaXiaEnterpriseTask task);


    /**
     * 查询任务，不在工作时间内的数据且正在进行的订单
     *
     * @param huaXiaEnterpriseAccount 企业账户
     * @param taskType                任务类型
     * @param startWorkTime           开始工作时间
     * @param endWorkTime             结束工作时间
     * @param effectTime              任务生效时间
     * @param maxCount                最大取出的数量
     * @return
     */
    List<HuaXiaEnterpriseTask> findTask(HuaXiaEnterpriseAccount huaXiaEnterpriseAccount, TaskType taskType, long startWorkTime, long endWorkTime, long effectTime, int maxCount);


    /**
     * 查询任务，不在工作时间内的数据且正在进行的订单
     *
     * @param taskType      任务类型
     * @param startWorkTime 开始工作时间
     * @param endWorkTime   结束工作时间
     * @param effectTime    任务生效时间
     * @param maxCount      最大取出的数量
     * @return
     */
    List<HuaXiaEnterpriseTask> findTask(TaskType taskType, long startWorkTime, long endWorkTime, long effectTime, int maxCount);


    /**
     * 修改任务状态为完成
     *
     * @param id
     * @return
     */
    boolean updateTaskStateFinish(String id);


    /**
     * 更新任务状态
     *
     * @param id
     * @return
     */
    boolean updateTaskState(String id, TaskState state);


    /**
     * 更新任务
     *
     * @param id
     * @param parameter
     * @return
     */
    boolean updateParameter(String id, HuaXiaEnterpriseTaskParameter parameter);

    long statisticsAmount(Long startTime, Long endTime,TaskType type );

    boolean existsByCheckTask(String chargeTaskId);


}
