package com.fast.dev.auth.center.server.core.dao.extend;

public interface SystemTaskDaoExtend {


    /**
     * 开始任务
     *
     * @param taskName
     * @param ttlTime
     * @return
     */
    Long task(String taskName, Long ttlTime);


    /**
     * 完成
     *
     * @param taskName
     */
    void finish(String taskName);


}
