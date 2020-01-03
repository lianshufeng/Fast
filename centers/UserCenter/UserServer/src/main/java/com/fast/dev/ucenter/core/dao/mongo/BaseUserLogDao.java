package com.fast.dev.ucenter.core.dao.mongo;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.ucenter.core.dao.mongo.extend.BaseUserLogExtend;
import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.domain.BaseUserLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseUserLogDao extends MongoDao<BaseUserLog>, BaseUserLogExtend {


    /**
     * 通过用户分页查询变更记录
     *
     * @param baseUser
     * @param pageable
     * @return
     */
    Page<BaseUserLog> findByBaseUser(BaseUser baseUser, Pageable pageable);


}
