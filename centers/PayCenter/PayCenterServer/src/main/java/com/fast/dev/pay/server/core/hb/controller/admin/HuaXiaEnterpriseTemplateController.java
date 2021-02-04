package com.fast.dev.pay.server.core.hb.controller.admin;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAutoChargeTemplateModel;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeTemplateService;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hb/ea/template")
public class HuaXiaEnterpriseTemplateController {

    @Autowired
    private HuaXiaEnterpriseAutoChargeTemplateService huaXiaEnterpriseAutoChargeTemplateService;

    @Autowired
    private AuthClientUserHelper userHelper;

    /**
     * 获取所有的套餐列表
     *
     * @param pageable
     * @return
     */
    @RequestMapping("list")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public Object list(Pageable pageable) {
        String epId = userHelper.getUser().getEnterPriseId();
        return ResultContent.buildContent(huaXiaEnterpriseAutoChargeTemplateService.list(epId, pageable));
    }


    /**
     * 新增或者更新套餐模板
     *
     * @param huaXiaEnterpriseAutoChargeTemplateModel
     * @return
     */
    @UserLog
    @RequestMapping("update")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public Object update(@RequestBody HuaXiaEnterpriseAutoChargeTemplateModel huaXiaEnterpriseAutoChargeTemplateModel) {
        //取出企业id
        String epId = userHelper.getUser().getEnterPriseId();
        huaXiaEnterpriseAutoChargeTemplateModel.setEpId(epId);
        return huaXiaEnterpriseAutoChargeTemplateService.update(huaXiaEnterpriseAutoChargeTemplateModel);
    }


    /**
     * 删除套餐模板
     *
     * @return
     */
    @UserLog
    @RequestMapping("remove")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public Object remove(String id) {
        return ResultContent.build(huaXiaEnterpriseAutoChargeTemplateService.remove(userHelper.getUser().getEnterPriseId(), id));
    }


}
