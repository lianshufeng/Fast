package com.fast.dev.pay.server.core.general.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.general.dao.extend.EnterprisePayAccountDaoExtend;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;

import java.util.List;

public interface EnterprisePayAccountDao extends MongoDao<EnterprisePayAccount>, EnterprisePayAccountDaoExtend {

    /**
     * 查询一个企业下的所有的支付账号
     *
     * @param epId
     * @return
     */
    List<EnterprisePayAccount> findByEnterpriseId(String epId);


    /**
     * 通过id查询企业支付账号
     *
     * @param id
     * @return
     */
    EnterprisePayAccount findTop1ById(String id);


    /**
     * 批量删除
     *
     * @param id
     * @return
     */
    long deleteByIdIn(String... id);


    /**
     * 批量删除企业支付账号，验证企业id
     *
     * @param id
     * @return
     */
    long deleteByEnterpriseIdAndIdIn(String epId, String[] id);


    /**
     * 企业支付账号不存在
     *
     * @param id
     * @return
     */
    boolean existsById(String id);

}
