package com.fast.dev.ucenter.service.service;

import com.fast.dev.ucenter.service.dao.UserLogDao;
import com.fast.dev.ucenter.service.domain.UserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {


    @Autowired
    private UserLogDao userLogDao;


    public void save(String userName){
        this.userLogDao.save(new UserLog(userName,new BigDecimal(System.currentTimeMillis())));
    }



}
