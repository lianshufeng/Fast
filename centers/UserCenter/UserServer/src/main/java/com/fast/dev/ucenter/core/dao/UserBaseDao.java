package com.fast.dev.ucenter.core.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.ucenter.core.dao.extend.UserBaseDaoExtend;
import com.fast.dev.ucenter.core.domain.UserBase;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 */
public interface UserBaseDao extends MongoDao<UserBase>, UserBaseDaoExtend {

    /**
     *  手机是否已注册过
     * @param phone
     * @return
     */
    boolean existsByPhone(String phone);


    /**
     *  用户名是否已注册
     * @param userName
     * @return
     */
    boolean existsByUserName(String userName);


}
