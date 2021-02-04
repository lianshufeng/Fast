package com.fast.dev.pay.server.core.hb.controller.superadmin;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAccountModel;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAccountService;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeContractService;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 华夏企业开通
 */
@RestController
@RequestMapping("hb/ea/admin")
public class HuaXiaEnterpriseAccountSuperAdminController {

    @Autowired
    private HuaXiaEnterpriseAccountService huaXiaEnterpriseAccountService;

    @Autowired
    private HuaXiaEnterpriseAutoChargeContractService huaXiaEnterpriseAutoChargeContractService;


    /**
     * 查询华夏企业账户列表
     *
     * @param huaXiaEnterpriseAccountModel
     * @param pageable
     * @return
     */
    @RequestMapping("list")
    @ResourceAuth(ResourceAuthConstant.SuperAdmin)
    public Object list(HuaXiaEnterpriseAccountModel huaXiaEnterpriseAccountModel, Pageable pageable) {
        return ResultContent.buildContent(this.huaXiaEnterpriseAccountService.list(huaXiaEnterpriseAccountModel, pageable));
    }

    /**
     * 更新或创建预开通的企业
     *
     * @param huaXiaEnterpriseAccountModel
     * @return
     */
    @UserLog
    @RequestMapping("update")
    @ResourceAuth(ResourceAuthConstant.SuperAdmin)
    public Object update(HuaXiaEnterpriseAccountModel huaXiaEnterpriseAccountModel) {
        Assert.hasText(huaXiaEnterpriseAccountModel.getEnterpriseName(), "企业名称不能为空");
        Assert.hasText(huaXiaEnterpriseAccountModel.getEnterprisePersonName(), "企业负责人名不能为空");
        Assert.hasText(huaXiaEnterpriseAccountModel.getEnterprisePhone(), "企业负责人的手机号码不能为空");
        if (huaXiaEnterpriseAccountModel.getWorkTime() != null) {
            Assert.state(huaXiaEnterpriseAccountModel.getWorkTime() > 0 && huaXiaEnterpriseAccountModel.getWorkTime() < 24 * 60, "工作时间必须在 0到24*60 分钟之间");
        }
        return this.huaXiaEnterpriseAccountService.update(huaXiaEnterpriseAccountModel);
    }


    /**
     * 下载公钥证书
     */
    @UserLog
    @RequestMapping("downPublicCert")
    @ResourceAuth(ResourceAuthConstant.SuperAdmin)
    public void downPublicCert(HttpServletRequest request, HttpServletResponse response, String id) {
        Assert.hasText(id, "华夏企业id不能为空");
        this.huaXiaEnterpriseAccountService.downPublicCert(request, response, id);
    }


    /**
     * 删除华夏企业
     * @param id
     * @return
     */
    @UserLog
    @RequestMapping("remove")
    @ResourceAuth(ResourceAuthConstant.SuperAdmin)
    public Object remove(String id) {
        return ResultContent.build(this.huaXiaEnterpriseAccountService.remove(id));
    }


    /**
     * 注册成为企业
     * @param id
     * @return
     */
    @UserLog
    @RequestMapping("register")
    @ResourceAuth(ResourceAuthConstant.SuperAdmin)
    public Object register(String id) {
        return ResultContent.build(this.huaXiaEnterpriseAccountService.register(id));
    }


    /**
     * 重置证书
     * @param id
     * @return
     */
    @UserLog
    @RequestMapping("resetCert")
    @ResourceAuth(ResourceAuthConstant.SuperAdmin)
    public Object resetCert(String id) {
        return ResultContent.build(this.huaXiaEnterpriseAccountService.resetCert(id));
    }


    /**
     * 统计数据
     * @param startTime
     * @param endTime
     * @return
     */
    @UserLog
    @RequestMapping("statistics")
    @ResourceAuth(ResourceAuthConstant.SuperAdmin)
    public Object statistics(Long startTime,Long endTime) {
        return ResultContent.build(ResultState.Success,this.huaXiaEnterpriseAutoChargeContractService.statisticsBySuperAdmin(startTime,endTime));
    }

}
