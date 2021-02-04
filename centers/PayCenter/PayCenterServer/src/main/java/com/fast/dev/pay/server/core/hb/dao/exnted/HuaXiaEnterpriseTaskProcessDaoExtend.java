package com.fast.dev.pay.server.core.hb.dao.exnted;

public interface HuaXiaEnterpriseTaskProcessDaoExtend {

    /**
     * 锁定任务
     * @param taskId
     * @return
     */
    boolean lockTask(String taskId);

    /**
     * 更新TTL
     *
     * @param taskId
     */
    void updateTTL(String... taskId);


    /**
     * 删除任务
     *
     * @param taskId
     */
    long removeTask(String... taskId);


}
