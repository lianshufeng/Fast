package com.fast.dev.pay.server.core.general.service.pay.impl.ailipay;

import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.pay.client.model.PreOrderModel;
import com.fast.dev.pay.client.model.PrePayOrderModel;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.client.type.PayMethod;
import com.fast.dev.pay.server.core.general.helper.ali.AliPayHelper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝面对面支付
 */
@Service
public class AliPayFaceToFaceSuperPaySupportService extends AliPaySuperPaySupportService {

    @Override
    public PayMethod payMethod() {
        return PayMethod.AliPayFaceToFace;
    }


    @Override
    @SneakyThrows
    public ResultContent<PreOrderModel> execute(AliPayHelper aliPayHelper, PrePayOrderModel payOrder) {
        //订单
        final String orderId = payOrder.getOrderId();

        AlipayTradePrecreateResponse response = aliPayHelper.faceToFace(orderId, payOrder.getProduct().getTitle(), orderId, payOrder.getPrice());

        //处理响应值
        Map<String, Object> responseMap = JsonUtil.toObject(response.getHttpBody(), Map.class);

        //取出返回数据
        final Object alipay_trade_precreate_response = responseMap.get("alipay_trade_precreate_response");

        PreOrderModel preOrderModel = new PreOrderModel();
        if (ResponseChecker.success(response)) {
            preOrderModel.setPlatformOrder(orderId);

            //返回的数据
            preOrderModel.setSupport(BeanUtil.toMap(alipay_trade_precreate_response));
        } else {
            preOrderModel.setError(BeanUtil.toMap(alipay_trade_precreate_response));
        }
        return ResultContent.build(ResponseChecker.success(response) ? ResultState.Success : ResultState.OrderError, preOrderModel);
    }


}
