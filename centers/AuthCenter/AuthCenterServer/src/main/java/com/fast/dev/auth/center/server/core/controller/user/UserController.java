package com.fast.dev.auth.center.server.core.controller.user;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.auth.security.model.UserParmModel;
import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {


    @Autowired
    private AuthClientUserHelper userHelper;


    /**
     * 获取当前用户的权限信息
     *
     * @return
     */
    @RequestMapping("get")
    @ResourceAuth(value = ResourceAuthConstant.User, remark = "用户权限")
    public InvokerResult<ResultContent> getAuth() {
        UserParmModel userParmModel = this.userHelper.getUser();
        CurrentUserModel currentUserModel = new CurrentUserModel();
        BeanUtils.copyProperties(userParmModel, currentUserModel, "sToken");
        return InvokerResult.notNull(ResultContent.buildContent(currentUserModel));
    }


    static class CurrentUserModel extends UserParmModel {
    }

}
