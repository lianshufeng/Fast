package com.fast.dev.auth.center.server.core.controller.user;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.auth.client.service.EnterpriseService;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user/enterprise")
public class UserEnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private AuthClientUserHelper userHelper;

    @Autowired
    private UserLogHelper userLogHelper;

    /**
     * 重置企业SK
     *
     * @return
     */
    @UserLog(value = ResourceAuthConstant.Admin)
    @RequestMapping("restSK")
    @ResourceAuth(value = ResourceAuthConstant.Admin, remark = "管理员")
    public Object restSK() {
        String epId = userHelper.getUser().getEnterPriseId();
        this.userLogHelper.log("epId", epId);
        return this.enterpriseService.resetSK(epId);
    }


}
