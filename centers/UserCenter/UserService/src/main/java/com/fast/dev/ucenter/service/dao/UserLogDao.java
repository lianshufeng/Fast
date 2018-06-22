package com.fast.dev.ucenter.service.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.ucenter.service.domain.UserLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;


public interface UserLogDao extends MongoDao<UserLog> ,UserLogDaoExtend{


    /**
     * 分页查询
     *
     * @param name
     * @param pageable
     * @return
     */
    Page<UserLog> findByName(String name, Pageable pageable);














}
