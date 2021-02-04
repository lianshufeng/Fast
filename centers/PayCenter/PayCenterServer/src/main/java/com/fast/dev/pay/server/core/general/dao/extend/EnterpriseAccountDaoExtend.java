package com.fast.dev.pay.server.core.general.dao.extend;

import com.fast.dev.pay.server.core.general.domain.EnterpriseAccount;

public interface EnterpriseAccountDaoExtend {

    /**
     * 查询或保存企业,自动生产企业code
     *
     * @param enterpriseId
     * @return
     */
    EnterpriseAccount findAndSave(String enterpriseId);

}
