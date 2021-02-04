package com.fast.dev.data.token.dao;

import com.fast.dev.data.token.dao.extend.ResourceTokenDaoExtend;
import com.fast.dev.data.token.domain.ResourceToken;
import com.fast.dev.data.mongo.dao.MongoDao;

/**
 * 资源令牌的Dao
 */
public interface ResourceTokenDao extends MongoDao<ResourceToken>, ResourceTokenDaoExtend {


    /**
     * 排序查询第一个资源令牌
     *
     * @param resourceName
     * @return
     */
    ResourceToken findTop1ByResourceNameAndTypeOrderByCounterAsc(String resourceName, ResourceToken.ResourceType type);


    /**
     * 删除资源与计数器
     *
     * @param resourceName
     * @param counter
     * @return
     */
    long removeByResourceNameAndCounterAndType(String resourceName, long counter, ResourceToken.ResourceType type);


    /**
     * 是否存在
     *
     * @param resoueceName
     * @param type
     * @return
     */
    boolean existsByResourceNameAndType(String resoueceName, ResourceToken.ResourceType type);

}
