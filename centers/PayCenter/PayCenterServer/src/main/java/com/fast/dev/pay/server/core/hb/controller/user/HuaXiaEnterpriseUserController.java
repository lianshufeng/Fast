package com.fast.dev.pay.server.core.hb.controller.user;

import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeContractService;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeTemplateService;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeUserOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hb/ea/user")
public class HuaXiaEnterpriseUserController {

    @Autowired
    private HuaXiaEnterpriseAutoChargeUserOrderService huaXiaEnterpriseAutoChargeUserOrderService;

    @Autowired
    private HuaXiaEnterpriseAutoChargeTemplateService huaXiaEnterpriseAutoChargeTemplateService;

    @Autowired
    private HuaXiaEnterpriseAutoChargeContractService huaXiaEnterpriseAutoChargeContractService;

    /**
     * 读取模板订单
     *
     * @return
     */
    @UserLog
    @RequestMapping("readTemplateOrder")
    public Object readTemplateOrder(String hbCode, String templateCode) {
        Assert.hasText(hbCode, "企业编码不能为空");
        Assert.hasText(templateCode, "模板编码不能为空");
        return this.huaXiaEnterpriseAutoChargeTemplateService.readOrder(hbCode, templateCode);
    }


    /**
     * 读取用户订单
     *
     * @return
     */
    @UserLog
    @RequestMapping("readUserOrder")
    public Object readUserOrder(String id) {
        Assert.hasText(id, "用户订单id不能为空");
        return this.huaXiaEnterpriseAutoChargeUserOrderService.readOrder(id);
    }







}
