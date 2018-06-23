package com.fast.dev.ucenter.service.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.ucenter.service.dao.extend.UserLogDaoExtend;
import com.fast.dev.ucenter.service.domain.UserLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserLogDao extends MongoDao<UserLog> ,UserLogDaoExtend {


    /**
     * 分页查询
     *
     * @param name
     * @param pageable
     * @return
     */
    Page<UserLog> findByName(String name, Pageable pageable);














}
