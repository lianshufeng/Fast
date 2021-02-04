package com.fast.dev.auth.center.server.core.dao;

import com.fast.dev.auth.center.server.core.dao.extend.EnterpriseDaoExtend;
import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.data.mongo.dao.MongoDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnterpriseDao extends MongoDao<Enterprise>, EnterpriseDaoExtend {


    /**
     * 查询企业
     *
     * @param name
     * @param pageable
     * @return
     */
    Page<Enterprise> findByNameLike(String name, Pageable pageable);

    /**
     * 通过Id查询企业
     *
     * @param id
     * @return
     */
    Enterprise findTop1ById(String id);

    /**
     * 通过AK查询企业
     *
     * @param ak
     * @return
     */
    Enterprise findTop1ByAk(String ak);


    /**
     * 通过企业名称找到这个企业对象
     *
     * @param name
     * @return
     */
    Enterprise findTop1ByName(String name);

    /**
     * 查询这个企业是否已注册
     *
     * @param name
     * @return
     */
    boolean existsByName(String name);


    /**
     * 企业是否存在
     *
     * @param epId
     * @return
     */
    boolean existsById(String epId);


}
