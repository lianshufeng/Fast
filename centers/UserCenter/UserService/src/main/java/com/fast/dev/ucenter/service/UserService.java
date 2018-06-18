package com.fast.dev.ucenter.service;

import com.fast.dev.ucenter.dao.UserLogDao;
import com.fast.dev.ucenter.domain.UserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    @Autowired
    private UserLogDao userLogDao;


    public void save(String userName){
        this.userLogDao.save(new UserLog(userName,System.currentTimeMillis()));
    }



}
