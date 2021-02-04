package com.fast.dev.pay.server.core.hb.service.impl;

import com.fast.dev.pay.server.core.hb.aop.hbapi.HuaXiaEnterpriseHuaXiaApiServiceHelper;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTaskJournal;
import com.fast.dev.pay.server.core.hb.model.SuperAutoChargeContractModel;
import com.fast.dev.pay.server.core.hb.model.resp.OpenAcctResp;
import com.fast.dev.pay.server.core.hb.service.HuaXiaApiService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class HuaXiaApiServiceImplExecuteSuccess extends HuaXiaApiService {
    @Override
    public OpenAcctResp sendMessageNo(HuaXiaEnterpriseAccount huaXiaEnterpriseAccount, String phone) {
        return new OpenAcctResp();
    }

    @Autowired
    private HuaXiaEnterpriseHuaXiaApiServiceHelper huaXiaEnterpriseHuaXiaApiServiceHelper;

    @Override
    public ApiResult openAccount(SuperAutoChargeContractModel model) {
        String tradeNo = this.huaXiaEnterpriseHuaXiaApiServiceHelper.getTradeNo();
        System.out.println("tradeNo -> " + tradeNo);
        this.huaXiaEnterpriseHuaXiaApiServiceHelper.updateRequest(new HashMap<>() {{
            put("openAccount", "request");
        }});

        this.huaXiaEnterpriseHuaXiaApiServiceHelper.updateResponse(new HashMap<>() {{
            put("openAccount", "response");
        }});
        return ApiResult.success();
    }

    @Override
    public ApiResult freezeTask(HuaXiaEnterpriseTask task) {
        return ApiResult.success();
    }

    @Override
    public ApiResult chargeTask(HuaXiaEnterpriseTask task, HuaXiaEnterpriseTaskJournal taskJournal, long amount) {
        return ApiResult.success();
    }

    @Override
    public ApiResult checkChargeTask(HuaXiaEnterpriseTask task) {
        return ApiResult.success();
    }
}
