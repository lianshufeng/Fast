package com.fast.dev.pay.server.core.hb.controller.openapi;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.openapi.client.controller.OpenApiController;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAutoChargeUserOrderModel;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeUserOrderService;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HuaXiaEnterpriseUserOrderOpenApiController extends OpenApiController {

    @Autowired
    private HuaXiaEnterpriseAutoChargeUserOrderService huaXiaEnterpriseAutoChargeUserOrderService;

    @Autowired
    private AuthClientUserHelper userHelper;

    /**
     * 添加一个用户订单
     *
     * @return
     */
    @UserLog
    @RequestMapping("hb/ea/userorder/add")
    public Object add(@RequestBody HuaXiaEnterpriseAutoChargeUserOrderModel model) {
        Assert.hasText(model.getConsumePhone(), "消费者手机号码不能为空");
        Assert.notNull(model.getAutoChargeInfos(), "代扣信息不能为空");
        Assert.hasText(model.getName(), "用户订单名称不能为空");
        String epId = getEnterpriseId().getEpId();
        return huaXiaEnterpriseAutoChargeUserOrderService.add(epId, model);
    }


    /**
     * 订单列表
     * @param model
     * @return
     */
    @RequestMapping("hb/ea/userorder/list")
    public Object list(@RequestBody HuaXiaEnterpriseAutoChargeUserOrderModel model) {
        Pageable pageable = PageRequest.of(0,20);
        if (model.getPage() != null && model.getSize() != null){
            pageable = PageRequest.of(model.getPage(),model.getSize());
        }
        model.setEpId(getEnterpriseId().getEpId());
        return ResultContent.buildContent(huaXiaEnterpriseAutoChargeUserOrderService.list(model, pageable));
    }


}
