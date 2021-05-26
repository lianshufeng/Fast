package com.fast.dev.auth.center.server.core.controller.user;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.client.model.UserModel;
import com.fast.dev.auth.client.service.EnterpriseService;
import com.fast.dev.auth.client.service.UserService;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.auth.security.model.UserParmModel;
import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("user/enterprise")
public class UserEnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private AuthClientUserHelper userHelper;

    @Autowired
    private UserLogHelper userLogHelper;


    @Autowired
    private UserService userService;

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


    @RequestMapping("updateUserInfo")
    @UserLog("updateUserInfo")
    @ResourceAuth(value = ResourceAuthConstant.Admin)
    public InvokerResult<ResultContent> updateUserInfo(UserModel userModel) {
        Assert.hasText(userModel.getUid(), "用户id不能为空");
        //取出当前的企业
        String epId = userHelper.getUser().getEnterPriseId();
        return InvokerResult.notNull(this.userService.updateUser(epId, userModel));
    }


}
