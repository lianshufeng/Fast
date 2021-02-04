package com.fast.dev.pay.server.core.hb.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaEnterpriseTaskJournalDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTaskJournal;

import java.util.List;

public interface HuaXiaEnterpriseTaskJournalDao extends MongoDao<HuaXiaEnterpriseTaskJournal>, HuaXiaEnterpriseTaskJournalDaoExtend {


    /**
     * 查询流水
     *
     * @param id
     * @return
     */
    HuaXiaEnterpriseTaskJournal findTop1ById(String id);


//    /**
//     * 创建时间，倒序取前3个的任务流水
//     *
//     * @param task
//     * @return
//     */
//    List<HuaXiaEnterpriseTaskJournal> findTop3ByHuaXiaEnterpriseTaskOrderByCreateTimeDesc(HuaXiaEnterpriseTask task);


    /**
     * 查询任务流水，默认只查10条数据，时间降序
     *
     * @param task
     * @return
     */
//    List<HuaXiaEnterpriseTaskJournal> findTop10ByHuaXiaEnterpriseTaskOrderByCreateTimeAsc(HuaXiaEnterpriseTask task);

    List<HuaXiaEnterpriseTaskJournal> findTop10ByHuaXiaEnterpriseTaskOrderByCreateTimeDesc(HuaXiaEnterpriseTask task);


    //    List<HuaXiaEnterpriseTaskJournal> findByHuaXiaEnterpriseTaskOrderByCreateTimeAsc(HuaXiaEnterpriseTask task);


    /**
     * 是否存在华夏的流水任务
     *
     * @param task
     * @return
     */
    boolean existsByHuaXiaEnterpriseTask(HuaXiaEnterpriseTask task);

}
