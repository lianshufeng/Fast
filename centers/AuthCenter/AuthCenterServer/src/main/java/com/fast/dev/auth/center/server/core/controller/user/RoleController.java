package com.fast.dev.auth.center.server.core.controller.user;

import com.fast.dev.auth.center.server.core.helper.UserCenterHelper;
import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.client.model.RoleModel;
import com.fast.dev.auth.client.service.RoleService;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthClientUserHelper userHelper;

    @Autowired
    private UserCenterHelper userCenterHelper;


    /**
     * 更新数据
     *
     * @param roleModel
     * @return
     */

    @UserLog(value = ResourceAuthConstant.Admin, parameter = {"#roleModel"})
    @RequestMapping("update")
    @ResourceAuth(value = ResourceAuthConstant.Admin, remark = "管理员")
    public InvokerResult<ResultContent> update(RoleModel roleModel) {
        //取当前登录用户的企业id
        roleModel.setEnterpriseId(this.userHelper.getUser().getEnterPriseId());
        return InvokerResult.notNull(this.roleService.updateRole(roleModel));
    }

    /**
     * 条件查询列表
     *
     * @param roleModel
     * @param pageable
     * @return
     */
    @RequestMapping("list")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public InvokerResult<ResultContent> list(RoleModel roleModel, @PageableDefault Pageable pageable) {
        roleModel.setEnterpriseId(this.userHelper.getUser().getEnterPriseId());
        return InvokerResult.notNull(this.roleService.list(roleModel, pageable));
    }


    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    @RequestMapping("remove")
    @ResourceAuth(ResourceAuthConstant.Admin)
    @UserLog(value = ResourceAuthConstant.Admin, parameter = {"#roleId"})
    public InvokerResult<ResultContent> remove(String roleId) {
        checkEnterpriseRole(roleId);
        return InvokerResult.notNull(this.roleService.removeRole(roleId));
    }


    /**
     * 在角色中添加用户
     *
     * @param roleId
     */
    @RequestMapping("addUserRole")
    @ResourceAuth(ResourceAuthConstant.Admin)
    @UserLog(value = ResourceAuthConstant.Admin, parameter = {"#roleId"})
    public InvokerResult<ResultContent> addUserRole(String roleId, HttpServletRequest request) {
        checkEnterpriseRole(roleId);
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
    @ResourceAuth(ResourceAuthConstant.Admin)
    @UserLog(value = ResourceAuthConstant.Admin, parameter = {"#roleId"})
    public InvokerResult<ResultContent> removeUserRole(String roleId, HttpServletRequest request) {
        checkEnterpriseRole(roleId);
        String uid = this.userCenterHelper.getUid(request);
        Assert.hasText(uid, "用户信息不正确,phone或者uid");
        return InvokerResult.notNull(this.roleService.removeUserRole(roleId, uid));
    }

    /**
     * 获取角色里的用户
     *
     * @param roleId
     * @param pageable
     * @return
     */
    @RequestMapping("listRoleUser")
    @ResourceAuth(ResourceAuthConstant.Admin)
    @UserLog(value = ResourceAuthConstant.Admin, parameter = {"#roleId"})
    public InvokerResult<ResultContent> listRoleUser(String[] roleId, @PageableDefault Pageable pageable) {
        checkEnterpriseRole(roleId);
        return InvokerResult.notNull(this.roleService.listRoleUser(roleId, pageable));
    }


    /**
     * 检查当前企业能否操作其他的角色
     *
     *
     *
     * @param roleIds
     * @return
     */
    private void checkEnterpriseRole(String... roleIds) {
        for (String roleId : roleIds) {
            Assert.hasText(roleId, "角色id不能为空");
            RoleModel role = this.roleService.get(roleId).getContent();
            Assert.notNull(role, "角色id不正确");
            String epId = this.userHelper.getUser().getEnterPriseId();
            Assert.hasText(epId, "企业id的参数为空");
            Assert.state(epId.equals(role.getEnterpriseId()), "企业ID不匹配,无法操作此角色");
        }
    }


}
