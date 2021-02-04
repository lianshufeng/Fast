package com.fast.dev.pay.server.core.general.controller.callback;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.pay.server.core.general.domain.PayOrder;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelper;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelperCacheManager;
import com.fast.dev.pay.server.core.general.service.impl.PayServiceImpl;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class SuperCallBackController {

    @Autowired
    private PayServiceImpl payService;


    @Autowired
    protected PaySupportHelperCacheManager paySupportHelperCacheManager;

    /**
     * 查询订单
     *
     * @return
     */
    @SneakyThrows
    public PayOrder queryPayOrder(HttpServletResponse response, String orderId) {
        //根据订单查询支付账号
        PayOrder payOrder = this.payService.getLocalOrder(orderId);
        if (payOrder == null) {
            log.info("错误的订单 : {}", orderId);
            response.sendError(404);
            return null;
        }
        return payOrder;
    }


    /**
     * 返回成功
     *
     * @param response
     */
    @SneakyThrows
    protected void sendSuccess(HttpServletResponse response) {
        Map<String, String> data = new HashMap<String, String>() {
            {
                put("code", "SUCCESS");
                put("message", "回调成功");
            }
        };


        @Cleanup PrintWriter printWriter = response.getWriter();
        printWriter.write(JsonUtil.toJson(data));
        printWriter.flush();

        //响应成功
        response.setStatus(200);
    }


    /**
     * 获取请求参数,并验证签名
     */
    @SneakyThrows
    public Object getRequestBody(HttpServletRequest request, PayOrder payOrder) {
        //取出支付宝的支付工具
        final PaySupportHelper paySupportHelper = this.paySupportHelperCacheManager.get(payOrder.getEnterprisePayAccount(), getPaySupportHelper());
        return paySupportHelper.readObjectFromRequest(request, payOrder.getMethod().getCallBack());
    }


    /**
     * 获取第三方支付助手
     *
     * @return
     */
    public abstract Class<? extends PaySupportHelper> getPaySupportHelper();

}
