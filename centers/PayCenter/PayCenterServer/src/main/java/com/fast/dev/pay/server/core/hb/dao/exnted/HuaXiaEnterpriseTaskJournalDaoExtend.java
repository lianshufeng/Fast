package com.fast.dev.pay.server.core.hb.dao.exnted;

import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTaskJournal;

public interface HuaXiaEnterpriseTaskJournalDaoExtend {


    /**
     * 创建一笔流水
     */
    HuaXiaEnterpriseTaskJournal buildTaskJournal();


    /**
     * 更新流水
     */
    void updateTaskJournal(HuaXiaEnterpriseTaskJournal journal);


}
