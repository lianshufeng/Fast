package com.fast.dev.ucenter.service.dao;

import com.fast.dev.ucenter.service.domain.UserLog;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface UserLogDao extends MongoRepository<UserLog, String> {


    /**
     * 分页查询
     *
     * @param name
     * @param pageable
     * @return
     */
    Page<UserLog> findByName(String name, Pageable pageable);










}
