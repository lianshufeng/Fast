package com.fast.dev.pay.server.core.general.controller.user;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.pay.client.model.EnterprisePayAccountModel;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.server.core.general.service.impl.EnterpriseAccountServiceImpl;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("enterprise/pay")
public class EnterpriseAdminAccountController {

    @Autowired
    private EnterpriseAccountServiceImpl enterpriseAccountService;

    @Autowired
    private AuthClientUserHelper userHelper;


    /**
     * 新增或者更新企业支付账号
     */
    @UserLog(value = ResourceAuthConstant.Admin, parameter = {"#payAccountModel"})
    @ResourceAuth(value = ResourceAuthConstant.Admin, remark = "管理员")
    @RequestMapping(value = "update")
    ResultContent update(@RequestBody EnterprisePayAccountModel payAccountModel) {
        payAccountModel.setEnterpriseId(userHelper.getUser().getEnterPriseId());
        return this.enterpriseAccountService.update(payAccountModel);
    }


    /**
     * 删除企业支付账号
     *
     * @return
     */
    @UserLog(value = ResourceAuthConstant.Admin, parameter = {"#id"})
    @RequestMapping(value = "remove", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent remove(@RequestParam("id") String... id) {
        return this.enterpriseAccountService.remove(this.userHelper.getUser().getEnterPriseId(), id);
    }


    /**
     * 获取该企业下的所有支付账号
     *
     * @return
     */
    @ResourceAuth(value = ResourceAuthConstant.Admin, remark = "管理员")
    @RequestMapping(value = "list", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<List<EnterprisePayAccountModel>> list() {
        return this.enterpriseAccountService.list(this.userHelper.getUser().getEnterPriseId());
    }


}
