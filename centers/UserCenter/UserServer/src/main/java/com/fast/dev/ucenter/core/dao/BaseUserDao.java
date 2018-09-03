package com.fast.dev.ucenter.core.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.ucenter.core.dao.extend.BaseUserDaoExtend;
import com.fast.dev.ucenter.core.domain.BaseUser;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 */
public interface BaseUserDao extends MongoDao<BaseUser>, BaseUserDaoExtend {

    /**
     * 手机是否已注册过
     *
     * @param phone
     * @return
     */
    boolean existsByPhone(String phone);


    /**
     * 用户名是否已注册
     *
     * @param userName
     * @return
     */
    boolean existsByUserName(String userName);

    /**
     * 通过手机查询
     *
     * @param phone
     * @return
     */
    BaseUser findTop1ByPhone(String phone);


    /**
     * 通过手机查询
     *
     * @param userName
     * @return
     */
    BaseUser findTop1ByUserName(String userName);


    /**
     * 通过邮箱
     *
     * @param mail
     * @return
     */
    BaseUser findTop1ByMail(String mail);


    /**
     * 通过身份证
     *
     * @param idCard
     * @return
     */
    BaseUser findTop1ByIdCard(String idCard);


    /**
     * 根据用户id查询该用户详情
     * @param uid
     * @return
     */
    BaseUser findTop1ById(String uid);


}
