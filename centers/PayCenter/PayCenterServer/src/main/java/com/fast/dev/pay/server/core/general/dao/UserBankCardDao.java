package com.fast.dev.pay.server.core.general.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.general.dao.extend.UserBankCardDaoExtend;
import com.fast.dev.pay.server.core.general.domain.UserBankCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserBankCardDao extends MongoDao<UserBankCard>, UserBankCardDaoExtend {

    /**
     * 查询用户
     *
     * @param uid
     * @param pageable
     * @return
     */
    Page<UserBankCard> findByUidAndDeleteFalse(String uid, Pageable pageable);


    /**
     * 删除银行卡
     *
     * @param id
     * @return
     */
    void deleteById(String id);


    /**
     * 通过id查询卡的信息
     *
     * @param id
     * @return
     */
    UserBankCard findTop1ById(String id);


}
