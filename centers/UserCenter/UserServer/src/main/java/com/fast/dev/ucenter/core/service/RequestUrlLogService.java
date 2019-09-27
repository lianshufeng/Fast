package com.fast.dev.ucenter.core.service;

import com.fast.dev.ucenter.core.dao.mongo.RequestUrlLogDao;
import com.fast.dev.ucenter.core.domain.RequestUrlLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestUrlLogService {

    @Autowired
    private RequestUrlLogDao requestUrlLogDao;


    /**
     * 添加日志
     *
     * @param remoteIp
     * @param uri
     * @param parameter
     */
    public void add(final String remoteIp, final String uri, final String queryString, final Object parameter, final long usedTime) {
        final RequestUrlLog requestUrlLog = new RequestUrlLog();
        requestUrlLog.setRemoteIp(remoteIp);
        requestUrlLog.setUri(uri);
        requestUrlLog.setParameter(parameter);
        requestUrlLog.setUsedTime(usedTime);
        requestUrlLog.setQueryString(queryString);
        this.requestUrlLogDao.save(requestUrlLog);
    }


}
