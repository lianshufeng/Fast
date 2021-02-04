package com.fast.dev.pay.server.core.hb.dao.exnted;

import com.fast.dev.pay.server.core.hb.domain.HuaXiaApiJournal;

public interface HuaXiaApiJournalDaoExtend {


    /**
     * 创建流水
     *
     * @return
     */
    HuaXiaApiJournal create(String methodName);


    /**
     * 更新请求
     *
     * @param id
     * @param request
     * @return
     */
    boolean updateRequest(String id, String request);

    /**
     * 更新响应
     *
     * @param id
     * @param response
     * @return
     */
    boolean updateResponse(String id, String response);


    /***
     * 更新TTL时间
     * @param id
     * @param ttlTime
     * @return
     */
    boolean updateTTL(String id,long ttlTime);


}
