package com.fast.dev.pay.server.core.hb.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaEnterpriseAutoChargeTemplateDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HuaXiaEnterpriseAutoChargeTemplateDao extends MongoDao<HuaXiaEnterpriseAutoChargeTemplate>, HuaXiaEnterpriseAutoChargeTemplateDaoExtend {

    /**
     * 获取企业套餐列表
     *
     * @param pageable
     * @return
     */
    Page<HuaXiaEnterpriseAutoChargeTemplate> findAll(Pageable pageable);

    /**
     * 通过模板id找到该模板
     *
     * @param id
     * @return
     */
    HuaXiaEnterpriseAutoChargeTemplate findTop1ById(String id);


    /**
     * 删除一个模板
     *
     * @param id
     * @return
     */
    long removeByHuaXiaEnterpriseAccountAndId(HuaXiaEnterpriseAccount huaXiaEnterpriseAccount, String id);


    /**
     * 查询一个模板
     *
     * @param huaXiaEnterpriseAccount
     * @param code
     * @return
     */
    HuaXiaEnterpriseAutoChargeTemplate findByHuaXiaEnterpriseAccountAndCode(HuaXiaEnterpriseAccount huaXiaEnterpriseAccount, String code);

    /**
     * 分页查询所有模板
     *
     * @param pageable
     * @return
     */
    Page<HuaXiaEnterpriseAutoChargeTemplate> findByHuaXiaEnterpriseAccount(HuaXiaEnterpriseAccount account, Pageable pageable);

}
