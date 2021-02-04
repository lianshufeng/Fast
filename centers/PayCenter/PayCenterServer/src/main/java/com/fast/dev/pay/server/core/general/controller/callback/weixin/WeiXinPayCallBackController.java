package com.fast.dev.pay.server.core.general.controller.callback.weixin;

import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.pay.client.support.weixin.callback.WeiXinCallBack;
import com.fast.dev.pay.server.core.general.controller.callback.SuperCallBackController;
import com.fast.dev.pay.server.core.general.domain.PayOrder;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelper;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelperCacheManager;
import com.fast.dev.pay.server.core.general.helper.weixin.WeiXinHelper;
import com.fast.dev.pay.server.core.general.service.callback.weixin.WeiXinPayCallBackService;
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
@RequestMapping("callback/weixin")
public class WeiXinPayCallBackController extends SuperCallBackController {

    @Autowired
    private WeiXinPayCallBackService weiXinCallBackService;

    @Autowired
    private PaySupportHelperCacheManager paySupportHelperCacheManager;


    @Autowired
    private UserLogHelper userLogHelper;


    /**
     * 微信的回调
     *
     * @return
     */
    //注意：此接防止被全局异常捕获返回 200
    @SneakyThrows
    @RequestMapping("pay/{orderId}")
    @UserLog(action = "WeiXinPayCallBack")
    public void weixinpay(HttpServletRequest request, HttpServletResponse response, @PathVariable String orderId) {
        log.info("callback : {} -> {}", "WeiXinPay", orderId);


        //根据订单查询支付账号
        PayOrder payOrder = super.queryPayOrder(response, orderId);
        if (payOrder == null) {
            return;
        }


        //开始执行回调方法
        CallBackUtil.execute(response, () -> {
            //取出返回数据
            WeiXinCallBack item = (WeiXinCallBack) super.getRequestBody(request, payOrder);

            //转换为数据对象
            Object data = decryptData(payOrder, item);

            //处理回调业务
            this.weiXinCallBackService.callback(payOrder, data);

            //响应
            super.sendSuccess(response);
        });
    }


    /**
     * 解密文本
     *
     * @return
     */
    private Object decryptData(final PayOrder payOrder, final WeiXinCallBack item) {
        //微信支付助手
        final WeiXinHelper weiXinHelper = this.paySupportHelperCacheManager.get(payOrder.getEnterprisePayAccount(), WeiXinHelper.class);

        //解密数据
        WeiXinCallBack.Resource resource = item.getResource();

        this.userLogHelper.log("sourceText", resource);
        String decryptText = weiXinHelper.decryptToText(resource.getAssociated_data(), resource.getNonce(), resource.getCiphertext());
        final Object data = jsonToObject(decryptText);
        this.userLogHelper.log("decryptText", data);

        return data;
    }

    @SneakyThrows
    private Object jsonToObject(String decryptText) {
        return JsonUtil.toObject(decryptText, Object.class);
    }

    @Override
    public Class<? extends PaySupportHelper> getPaySupportHelper() {
        return WeiXinHelper.class;
    }
}
