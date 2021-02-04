package com.fast.dev.pay.server.core.hb.controller.admin;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAutoChargeTemplateModel;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAutoChargeUserOrderModel;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeTemplateService;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeUserOrderService;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hb/ea/userorder")
public class HuaXiaEnterpriseUserOrderController {

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
    @RequestMapping("add")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public Object add(@RequestBody HuaXiaEnterpriseAutoChargeUserOrderModel model) {
        Assert.hasText(model.getConsumePhone(), "消费者手机号码不能为空");
        Assert.notNull(model.getAutoChargeInfos(), "代扣信息不能为空");
        Assert.hasText(model.getName(), "用户订单名称不能为空");
        String epId = userHelper.getUser().getEnterPriseId();
        return huaXiaEnterpriseAutoChargeUserOrderService.add(epId, model);
    }


    /**
     * 订单列表
     *
     * @param pageable
     * @return
     */
    @RequestMapping("list")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public Object list(HuaXiaEnterpriseAutoChargeUserOrderModel model, Pageable pageable) {
        model.setEpId(userHelper.getUser().getEnterPriseId());
        return ResultContent.buildContent(huaXiaEnterpriseAutoChargeUserOrderService.list(model, pageable));
    }


}
