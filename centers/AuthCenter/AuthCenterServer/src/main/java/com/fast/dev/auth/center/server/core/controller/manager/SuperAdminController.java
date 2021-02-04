package com.fast.dev.auth.center.server.core.controller.manager;

import com.fast.dev.auth.center.server.core.helper.UserCenterHelper;
import com.fast.dev.auth.center.server.core.service.impl.UserAuthServiceImpl;
import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.core.util.result.InvokerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("manager/super/admin")
public class SuperAdminController {

    @Autowired
    private UserCenterHelper userCenterHelper;

    @Autowired
    private UserAuthServiceImpl userAuthService;

    @Autowired
    private UserLogHelper userLogHelper;

    /**
     * 添加超级管理员
     */
    @RequestMapping("add")
    @UserLog("SuperAdmin")
    public InvokerResult add(HttpServletRequest request) {
        String uid = userCenterHelper.getUid(request);
        Assert.hasText(uid, "用户id不能为空");
        this.userLogHelper.log("user", uid);
        return InvokerResult.notNull(this.userAuthService.add(uid, ResourceAuthConstant.SuperAdmin));
    }


    /**
     * 删除管理员
     *
     * @param request
     * @return
     */
    @RequestMapping("remove")
    @UserLog("SuperAdmin")
    public InvokerResult remove(HttpServletRequest request) {
        String uid = userCenterHelper.getUid(request);
        Assert.hasText(uid, "用户id不能为空");
        this.userLogHelper.log("user", uid);
        return InvokerResult.notNull(this.userAuthService.remove(uid, ResourceAuthConstant.SuperAdmin));
    }


}
