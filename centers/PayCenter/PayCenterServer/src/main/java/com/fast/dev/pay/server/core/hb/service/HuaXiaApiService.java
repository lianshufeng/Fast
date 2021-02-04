package com.fast.dev.pay.server.core.hb.service;


import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTaskJournal;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAutoChargeContractModel;
import com.fast.dev.pay.server.core.hb.model.SuperAutoChargeContractModel;
import com.fast.dev.pay.server.core.hb.model.req.OpenAcctReq;
import com.fast.dev.pay.server.core.hb.model.resp.OpenAcctResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public abstract class HuaXiaApiService {




    /**
     * 发送短信
     *
     */
    public abstract OpenAcctResp sendMessageNo(HuaXiaEnterpriseAccount huaXiaEnterpriseAccount, String phone);

    /**
     * 开户
     *
     * @param model
     */
    public abstract ApiResult openAccount(SuperAutoChargeContractModel model);

    /**
     * 冻结
     *
     * @param task
     */
    public abstract ApiResult freezeTask(HuaXiaEnterpriseTask task);


    /**
     * 执行扣款任务
     *
     * @param task
     */
    public abstract ApiResult chargeTask(HuaXiaEnterpriseTask task, HuaXiaEnterpriseTaskJournal taskJournal, long amount);


    /**
     * 检查扣款是否成功
     *
     * @param task
     */
    public abstract ApiResult checkChargeTask(HuaXiaEnterpriseTask task);


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResult {

        //是否成功
        private boolean success;

        //失败原因
        private String failReason;

        /**
         * 成功
         *
         * @return
         */
        public static ApiResult success() {
            return new ApiResult(true, null);
        }

        /**
         * 失败
         *
         * @param reason
         * @return
         */
        public static ApiResult fail(String reason) {
            return new ApiResult(false, reason);
        }

    }

}
