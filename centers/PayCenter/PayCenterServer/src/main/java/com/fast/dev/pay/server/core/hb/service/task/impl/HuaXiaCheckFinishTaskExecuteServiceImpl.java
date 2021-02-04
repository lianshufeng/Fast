package com.fast.dev.pay.server.core.hb.service.task.impl;

import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.service.task.HuaXiaTaskExecuteService;
import com.fast.dev.pay.server.core.hb.type.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 检查合同是否已经完成
 */
@Slf4j
@Service
public class HuaXiaCheckFinishTaskExecuteServiceImpl extends HuaXiaTaskExecuteService {

    @Override
    public TaskType taskType() {
        return TaskType.CheckFinish;
    }

    @Override
    public void execute(HuaXiaEnterpriseTask task) {
        super.processService.checkFinishTask(task);
    }


}
