package com.fast.dev.auth.center.server.core.controller.admin;

import com.fast.dev.auth.center.server.core.helper.UserCenterHelper;
import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.auth.client.service.EnterpriseService;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("enterprise")
public class EnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private UserCenterHelper userCenterHelper;

    @Autowired
    private UserLogHelper userLogHelper;

    /**
     * 翻页查询所有的
     *
     * @param pageable
     * @return
     */
    @RequestMapping("list")
    @ResourceAuth(ResourceAuthConstant.SuperAdmin)
    public Object list(@ModelAttribute EnterpriseModel model, @PageableDefault Pageable pageable) {
        return this.enterpriseService.list(model, pageable);
    }


    /**
     * 添加企业
     *
     * @param model
     * @return
     */
    @RequestMapping("add")
    @UserLog(value = ResourceAuthConstant.SuperAdmin, parameter = {"#model"})
    @ResourceAuth(ResourceAuthConstant.SuperAdmin)
    public Object add(EnterpriseModel model, HttpServletRequest request) {
        String ownerUid = userCenterHelper.getUid(request);
        Assert.hasText(ownerUid, "拥有者参数的 phone 或者 uid 不能为空");
        userLogHelper.log("ownerUid", ownerUid);
        return this.enterpriseService.add(model, ownerUid);
    }

    /**
     * 重置企业的SK
     *
     * @param epId 企业id
     * @return
     */
    @RequestMapping("resetSK")
    @UserLog(value = ResourceAuthConstant.SuperAdmin, parameter = {"#epId"})
    @ResourceAuth(ResourceAuthConstant.SuperAdmin)
    public Object resetSK(String epId) {
        Assert.hasText(epId,"企业id不能为空");
        return this.enterpriseService.resetSK(epId);
    }

}
