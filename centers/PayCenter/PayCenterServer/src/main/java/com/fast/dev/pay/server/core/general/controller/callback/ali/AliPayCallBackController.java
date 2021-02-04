package com.fast.dev.pay.server.core.general.controller.callback.ali;

import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.pay.server.core.general.controller.callback.SuperCallBackController;
import com.fast.dev.pay.server.core.general.domain.PayOrder;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelper;
import com.fast.dev.pay.server.core.general.helper.ali.AliPayHelper;
import com.fast.dev.pay.server.core.general.service.callback.alipay.ALiPayCallBackService;
import com.fast.dev.pay.server.core.util.CallBackUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("callback/ali")
public class AliPayCallBackController extends SuperCallBackController {

    @Autowired
    private ALiPayCallBackService aLiPayCallBackService;


    /**
     * 支付宝回调
     */
    @SneakyThrows
    @RequestMapping("pay/{orderId}")
    @UserLog(action = "ALiPayCallBack")
    public void alipay(HttpServletRequest request, HttpServletResponse response, @PathVariable String orderId) {
        log.info("callback : {} -> {}", "AliPay", orderId);

        //根据订单查询支付账号
        PayOrder payOrder = super.queryPayOrder(response, orderId);
        if (payOrder == null) {
            return;
        }


        //开始执行回调方法
        CallBackUtil.execute(response, () -> {
            //取出数据
            Object item = getRequestBody(request, payOrder);

            // 处理回调
            this.aLiPayCallBackService.callback(payOrder, item);

            //回调成功
            super.sendSuccess(response);
        });
    }


    @Override
    public Class<? extends PaySupportHelper> getPaySupportHelper() {
        return AliPayHelper.class;
    }
}
