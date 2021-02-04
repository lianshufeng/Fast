package com.fast.dev.auth.center.server.core.service.impl;

import com.fast.dev.auth.center.server.core.dao.UserLogDao;
import com.fast.dev.auth.client.log.model.UserLogModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLogServiceImpl {

    @Autowired
    private UserLogDao userLogDao;

    /**
     * 添加日志
     *
     * @param userLogModel
     * @return
     */
    public boolean add(UserLogModel... userLogModel) {
        return this.userLogDao.add(userLogModel);
    }


}
