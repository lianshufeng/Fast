package com.fast.dev.pay.server.core.hb.dao.exnted;

import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAccountModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HuaXiaEnterpriseAccountDaoExtend {

    /**
     * 分页条件查询
     *
     * @param huaXiaEnterpriseAccountModel
     * @param pageable
     * @return
     */
    Page<HuaXiaEnterpriseAccount> list(HuaXiaEnterpriseAccountModel huaXiaEnterpriseAccountModel, Pageable pageable);


    /**
     * 查询满足工作的企业
     *
     * @param max
     * @return
     */
    List<HuaXiaEnterpriseAccount> findWorkEnterprise(int max, int workTime);



    /**
     * 通过企业id查询到华夏企业账户
     *
     * @param epId
     * @return
     */
    HuaXiaEnterpriseAccount findByEpId(String epId);
}
