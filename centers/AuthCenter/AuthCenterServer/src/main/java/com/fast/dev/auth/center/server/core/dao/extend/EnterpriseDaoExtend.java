package com.fast.dev.auth.center.server.core.dao.extend;

import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.auth.client.type.ResultState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnterpriseDaoExtend {

    /**
     * 模糊查询
     *
     * @param pageable
     * @return
     */
    Page<Enterprise> list(EnterpriseModel enterpriseModel, Pageable pageable);


    /**
     * 更新
     *
     * @param enterpriseModel
     * @return
     */
    boolean update(EnterpriseModel enterpriseModel);


    /**
     * 重置Info的索引
     */
    void reIndexInfo();


}
