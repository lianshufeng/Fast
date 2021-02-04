package com.fast.dev.auth.center.server.core.dao;

import com.fast.dev.auth.center.server.core.dao.extend.FamilyDaoExtend;
import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.auth.center.server.core.domain.Family;
import com.fast.dev.data.mongo.dao.MongoDao;

public interface FamilyDao extends MongoDao<Family>, FamilyDaoExtend {


    /**
     * 查询当前企业下的当前用户是否拥有家庭组
     *
     * @param uid
     * @param enterprise
     * @return
     */
    boolean existsByOwnerAndEnterprise(String uid, Enterprise enterprise);


    /**
     * 通过id查询到家庭
     *
     * @param id
     * @return
     */
    Family findTop1ById(String id);


    /**
     * 删除家庭组
     *
     * @param id
     */
    void removeById(String id);


}
