package com.fast.dev.pay.server.core.general.dao.extend;

import com.fast.dev.pay.client.model.EnterprisePayAccountModel;

public interface EnterprisePayAccountDaoExtend {

    /**
     * 新增或者更新企业模型
     *
     * @param enterprisePayAccountModel
     * @return
     */
    String update(EnterprisePayAccountModel enterprisePayAccountModel);


}
