package com.fast.dev.pay.server.core.hb.controller.openapi;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.openapi.client.controller.OpenApiController;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAccountDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAccountModel;
import com.fast.dev.pay.server.core.hb.model.StatisticsParam;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAccountService;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAutoChargeContractService;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseTaskManagerService;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 华夏企业开通
 */
@RestController
public class HuaXiaEnterpriseAccountAdminOpenApiController extends OpenApiController {

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
    @RequestMapping("hb/ea/doJob")
    public Object doJob() {
        //查询到当前企业账户
        HuaXiaEnterpriseAccount account = this.huaXiaEnterpriseAccountDao.findByEpId(getEnterpriseId().getEpId());
        //立即执行此任务
        huaXiaEnterpriseTaskManagerService.executeEnterpriseTask(account);
        return ResultContent.build(true);
    }


    /**
     * 获取当前企业的信息
     *
     * @return
     */
    @RequestMapping("hb/ea/get")
    public Object get() {
        return ResultContent.buildContent(this.huaXiaEnterpriseAccountService.get(getEnterpriseId().getEpId()));
    }


    /**
     * 更新或创建预开通的企业
     *
     * @param huaXiaEnterpriseAccountModel
     * @return
     */
    @UserLog
    @RequestMapping("hb/ea/update")
    public Object update(@RequestBody HuaXiaEnterpriseAccountModel huaXiaEnterpriseAccountModel) {
        huaXiaEnterpriseAccountModel.setCode(null);
        String epId = getEnterpriseId().getEpId();
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
     * 统计数据
     * @param param
     * @return
     */
    @UserLog
    @RequestMapping("hb/ea/statisticsByEp")
    public Object statisticsByEp(@RequestBody StatisticsParam param) {
        return ResultContent.build(ResultState.Success,this.huaXiaEnterpriseAutoChargeContractService.statisticsByAdmin(param.getStartTime(),param.getStartTime(),getEnterpriseId().getEpId()));
    }
}
