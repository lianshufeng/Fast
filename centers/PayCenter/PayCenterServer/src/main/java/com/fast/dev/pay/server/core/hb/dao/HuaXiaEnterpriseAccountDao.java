package com.fast.dev.pay.server.core.hb.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaEnterpriseAccountDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;

public interface HuaXiaEnterpriseAccountDao extends MongoDao<HuaXiaEnterpriseAccount>, HuaXiaEnterpriseAccountDaoExtend {

    /**
     * 通过id查询到预备企业
     *
     * @param id
     * @return
     */
    HuaXiaEnterpriseAccount findTop1ById(String id);


    /**
     * 删除企业,且是否已经注册过企业的
     *
     * @param id
     * @param exists
     * @return
     */
    long removeByIdAndEpIdExists(String id, boolean exists);



    /**
     * 通过企业编码查询到华夏企业账户
     * @param code
     * @return
     */
    HuaXiaEnterpriseAccount findByCode(String code);


}
