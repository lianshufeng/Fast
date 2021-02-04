package com.fast.dev.auth.client.log.service;

import com.fast.dev.auth.client.helper.UserLogOutputStreamHelper;
import com.fast.dev.auth.client.log.model.UserLogModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLogService {

    @Autowired
    private UserLogOutputStreamHelper userLogOutputStreamHelper;

    /**
     * 上载日志
     */
    public void push(UserLogModel userLogModel) {
        this.userLogOutputStreamHelper.publish(userLogModel);
    }


}
