package com.fast.dev.pay.server.core.hb.controller.admin;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAccountDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAccountModel;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAccountService;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeContractService;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseTaskManagerService;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 华夏企业开通
 */
@RestController
@RequestMapping("hb/ea")
public class HuaXiaEnterpriseAccountAdminController {

    @Autowired
    private HuaXiaEnterpriseTaskManagerService huaXiaEnterpriseTaskManagerService;

    @Autowired
    private HuaXiaEnterpriseAccountService huaXiaEnterpriseAccountService;

    @Autowired
    private AuthClientUserHelper userHelper;

    @Autowired
    private HuaXiaEnterpriseAccountDao huaXiaEnterpriseAccountDao;

    @Autowired
    private HuaXiaEnterpriseAutoChargeContractService huaXiaEnterpriseAutoChargeContractService;


    /**
     * 立即执行调度任务
     *
     * @return
     */
    @UserLog
    @RequestMapping("doJob")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public Object doJob() {
        //查询到当前企业账户
        HuaXiaEnterpriseAccount account = this.huaXiaEnterpriseAccountDao.findByEpId(this.userHelper.getUser().getEnterPriseId());
        //立即执行此任务
        huaXiaEnterpriseTaskManagerService.executeEnterpriseTask(account);
        return ResultContent.build(true);
    }


    /**
     * 获取当前企业的信息
     *
     * @return
     */
    @RequestMapping("get")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public Object get() {
        return ResultContent.buildContent(this.huaXiaEnterpriseAccountService.get(this.userHelper.getUser().getEnterPriseId()));
    }


    /**
     * 更新或创建预开通的企业
     *
     * @param huaXiaEnterpriseAccountModel
     * @return
     */
    @UserLog
    @RequestMapping("update")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public Object update(HuaXiaEnterpriseAccountModel huaXiaEnterpriseAccountModel) {
        String epId = this.userHelper.getUser().getEnterPriseId();
        huaXiaEnterpriseAccountModel.setEpId(epId);
        //设置当前操作的企业
        huaXiaEnterpriseAccountModel.setId(this.huaXiaEnterpriseAccountDao.findByEpId(epId).getId());

        if (huaXiaEnterpriseAccountModel.getWorkTime() != null) {
            Assert.state(huaXiaEnterpriseAccountModel.getWorkTime() > 0 && huaXiaEnterpriseAccountModel.getWorkTime() < 24 * 60, "工作时间必须在 0到24*60 分钟之间");
        }
        this.huaXiaEnterpriseAccountService.update(huaXiaEnterpriseAccountModel);
        return this.get();
    }


    /**
     * 下载公钥证书
     */
    @UserLog
    @RequestMapping("downPublicCert")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public void downPublicCert(HttpServletRequest request, HttpServletResponse response) {
        this.huaXiaEnterpriseAccountService.downPublicCert(request, response, this.huaXiaEnterpriseAccountDao.findByEpId(this.userHelper.getUser().getEnterPriseId()).getId());
    }

    /**
     * 统计数据
     * @param startTime
     * @param endTime
     * @return
     */
    @UserLog
    @RequestMapping("statisticsByEp")
    @ResourceAuth(ResourceAuthConstant.Admin)
    public Object statisticsByEp(Long startTime,Long endTime) {
        return ResultContent.build(ResultState.Success,this.huaXiaEnterpriseAutoChargeContractService.statisticsByAdmin(startTime,endTime,this.userHelper.getUser().getEnterPriseId()));
    }
}
