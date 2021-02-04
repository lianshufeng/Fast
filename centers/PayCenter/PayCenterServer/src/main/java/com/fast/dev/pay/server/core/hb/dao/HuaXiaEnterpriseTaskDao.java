package com.fast.dev.pay.server.core.hb.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaEnterpriseTaskDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeContract;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.type.TaskType;

import java.util.List;

public interface HuaXiaEnterpriseTaskDao extends MongoDao<HuaXiaEnterpriseTask>, HuaXiaEnterpriseTaskDaoExtend {


    HuaXiaEnterpriseTask findTop1ById(String id);

    long removeById(String id);


    /**
     * 查询合同的任务类型
     *
     * @return
     */
    List<HuaXiaEnterpriseTask> findByContractAndType(HuaXiaEnterpriseAutoChargeContract contract, TaskType type);


    boolean existsByContractAndTypeAndParent(HuaXiaEnterpriseAutoChargeContract contract,TaskType type,HuaXiaEnterpriseTask parentTask);

}
