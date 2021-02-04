package com.fast.dev.pay.server.core.hb.controller.openapi;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.openapi.client.controller.OpenApiController;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.server.core.hb.model.ContractRequestModel;
import com.fast.dev.pay.server.core.hb.model.ContractRequestOpenApiModel;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeContractService;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业合同管理
 */
@RestController
public class HuaXiaEnterpriseContractOpenApiController extends OpenApiController {

    @Autowired
    private HuaXiaEnterpriseAutoChargeContractService huaXiaEnterpriseAutoChargeContractService;


    @Autowired
    private AuthClientUserHelper userHelper;


    /**
     * 查询列表
     *
     * @param requestModel
     * @return
     */
    @RequestMapping("hb/ea/contract/list")
    public Object list(@RequestBody ContractRequestOpenApiModel requestModel) {
        String epId = getEnterpriseId().getEpId();
        Pageable pageable = PageRequest.of(0,20);
        if (requestModel.getPage() != null && requestModel.getSize() != null){
            pageable = PageRequest.of(requestModel.getPage(),requestModel.getSize());
        }
        return ResultContent.buildContent(huaXiaEnterpriseAutoChargeContractService.list(epId, requestModel, pageable));
    }


    /**
     * 查询合同详情
     */
    @RequestMapping("hb/ea/contract/details")
    public Object details(@RequestBody ContractRequestOpenApiModel requestModel) {
        String epId = getEnterpriseId().getEpId();
        return ResultContent.buildContent(huaXiaEnterpriseAutoChargeContractService.details(epId, requestModel.getId()));
    }


    /**
     * 终止任务
     *
     * @param requestModel
     * @return
     */
    @UserLog
    @RequestMapping("hb/ea/contract/cancel")
    public Object cancel(@RequestBody ContractRequestOpenApiModel requestModel) {
        String epId = getEnterpriseId().getEpId();
        return ResultContent.build(huaXiaEnterpriseAutoChargeContractService.cancel(epId, requestModel.getId()));
    }

}
