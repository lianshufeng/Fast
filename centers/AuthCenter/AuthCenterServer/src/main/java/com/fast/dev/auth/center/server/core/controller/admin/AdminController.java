package com.fast.dev.auth.center.server.core.controller.admin;


import com.fast.dev.auth.center.server.core.helper.UserCenterHelper;
import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.client.model.RoleModel;
import com.fast.dev.auth.client.service.RoleService;
import com.fast.dev.auth.client.type.ResultState;
import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("admin/role")
public class AdminController {

    @Autowired
    private RoleService roleService;


    @Autowired
    private UserCenterHelper userCenterHelper;


    /**
     * 超级管理员可以编辑任何企业的角色
     *
     * @param roleModel
     * @return
     */

    @RequestMapping("update")
    @UserLog(value = ResourceAuthConstant.SuperAdmin, parameter = {"#roleModel"})
    @ResourceAuth(value = ResourceAuthConstant.SuperAdmin, remark = "超级管理员")
    public InvokerResult<ResultContent> update(RoleModel roleModel) {
        Assert.hasText(roleModel.getEnterpriseId(), "企业id不能为空");
        return InvokerResult.notNull(this.roleService.updateRole(roleModel));
    }


    /**
     * 在角色中添加用户
     *
     * @param roleId
     */
    @RequestMapping("addUserRole")
    @UserLog(value = ResourceAuthConstant.SuperAdmin, parameter = {"#roleId"})
    @ResourceAuth(ResourceAuthConstant.SuperAdmin)
    public InvokerResult<ResultContent> addUserRole(String roleId, HttpServletRequest request) {
        String uid = this.userCenterHelper.getUid(request);
        Assert.hasText(uid, "用户信息不正确,phone或者uid");
        return InvokerResult.notNull(this.roleService.addUserRole(roleId, uid));
    }


    /**
     * 删除角色中的用户
     *
     * @param roleId
     * @param request
     */
    @RequestMapping("removeUserRole")
    @UserLog(value = ResourceAuthConstant.SuperAdmin, parameter = {"#roleId"})
    @ResourceAuth(ResourceAuthConstant.SuperAdmin)
    public InvokerResult<ResultContent> removeUserRole(String roleId, HttpServletRequest request) {
        String uid = this.userCenterHelper.getUid(request);
        Assert.hasText(uid, "用户信息不正确,phone或者uid");
        return InvokerResult.notNull(this.roleService.removeUserRole(roleId, uid));
    }

}
