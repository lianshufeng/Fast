package com.fast.dev.pay.server.core.hb.controller.admin;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.server.core.hb.model.ContractRequestModel;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAutoChargeContractModel;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeContractService;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业合同管理
 */
@RestController
@RequestMapping("hb/ea/contract")
public class HuaXiaEnterpriseContractController {

    @Autowired
    private HuaXiaEnterpriseAutoChargeContractService huaXiaEnterpriseAutoChargeContractService;


    @Autowired
    private AuthClientUserHelper userHelper;


    /**
     * 查询列表
     *
     * @param requestModel
     * @param pageable
     * @return
     */
    @RequestMapping("list")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public Object list(ContractRequestModel requestModel, Pageable pageable) {
        String epId = userHelper.getUser().getEnterPriseId();
        return ResultContent.buildContent(huaXiaEnterpriseAutoChargeContractService.list(epId, requestModel, pageable));
    }


    /**
     * 查询合同详情
     */
    @RequestMapping("details")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public Object details(String id) {
        String epId = userHelper.getUser().getEnterPriseId();
        return ResultContent.buildContent(huaXiaEnterpriseAutoChargeContractService.details(epId, id));
    }


    /**
     * 终止任务
     *
     * @param id
     * @return
     */
    @UserLog
    @RequestMapping("cancel")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public Object cancel(String id) {
        String epId = userHelper.getUser().getEnterPriseId();
        return ResultContent.build(huaXiaEnterpriseAutoChargeContractService.cancel(epId, id));
    }

}
