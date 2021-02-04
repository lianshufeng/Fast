package com.fast.dev.pay.server.core.hb.controller.openapi;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.openapi.client.controller.OpenApiController;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAutoChargeTemplateModel;
import com.fast.dev.pay.server.core.hb.model.PageModel;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeTemplateService;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HuaXiaEnterpriseTemplateOpenApiController extends OpenApiController {

    @Autowired
    private HuaXiaEnterpriseAutoChargeTemplateService huaXiaEnterpriseAutoChargeTemplateService;

    @Autowired
    private AuthClientUserHelper userHelper;

    /**
     * 获取所有的套餐列表
     *
     * @param param
     * @return
     */
    @RequestMapping("hb/ea/template/list")
    public Object list(PageModel param) {
        Pageable pageable = PageRequest.of(0,20);
        if (param.getPage() != null && param.getSize() != null){
            pageable = PageRequest.of(param.getPage(),param.getSize());
        }
        String epId = getEnterpriseId().getEpId();
        return ResultContent.buildContent(huaXiaEnterpriseAutoChargeTemplateService.list(epId, pageable));
    }


    /**
     * 新增或者更新套餐模板
     *
     * @param huaXiaEnterpriseAutoChargeTemplateModel
     * @return
     */
    @UserLog
    @RequestMapping("hb/ea/template/update")
    public Object update(@RequestBody HuaXiaEnterpriseAutoChargeTemplateModel huaXiaEnterpriseAutoChargeTemplateModel) {
        //取出企业id
        String epId = getEnterpriseId().getEpId();
        huaXiaEnterpriseAutoChargeTemplateModel.setEpId(epId);
        return huaXiaEnterpriseAutoChargeTemplateService.update(huaXiaEnterpriseAutoChargeTemplateModel);
    }


    /**
     * 删除套餐模板
     *
     * @return
     */
    @UserLog
    @RequestMapping("hb/ea/template/remove")
    public Object remove(@RequestBody HuaXiaEnterpriseAutoChargeTemplateModel model) {
        return ResultContent.build(huaXiaEnterpriseAutoChargeTemplateService.remove(getEnterpriseId().getEpId(), model.getId()));
    }


}
