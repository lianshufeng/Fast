package com.fast.dev.pay.server.core.general.helper;

import com.fast.dev.core.util.encode.HashUtil;
import com.fast.dev.pay.client.model.RefundOrderModel;
import com.fast.dev.pay.client.model.ResponseCloseOrderModel;
import com.fast.dev.pay.client.model.ResponseRefundOrderModel;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.type.PayState;
import com.fast.dev.pay.server.core.general.conf.PayConf;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;
import com.fast.dev.pay.server.core.general.domain.PayOrder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 三方支付的助手的父类
 */
public abstract class PaySupportHelper {

    @Autowired
    private PayConf payConf;

    @Setter
    @Getter
    private EnterprisePayAccount enterprisePayAccount;


    /**
     * 初始化
     */
    public abstract void after();


    /**
     * 通过request读取对象
     *
     * @param request
     * @param cls
     * @param <T>
     * @return
     */
    public abstract <T> T readObjectFromRequest(HttpServletRequest request, Class<? extends T> cls);


    /**
     * 查询三方支付订单信息
     *
     * @param payOrder
     * @return
     */
    public abstract Map<String, Object> queryOrder(PayOrder payOrder);


    /**
     * 关闭订单
     *
     * @param payOrder
     * @return
     */
    public abstract ResponseCloseOrderModel closeOrder(PayOrder payOrder);


    /**
     * 退款
     *
     * @param refundOrderModel
     * @return
     */
    public abstract ResponseRefundOrderModel refund(PayOrder payOrder, RefundOrderModel refundOrderModel);


    /**
     * 签名
     *
     * @param parm
     * @return
     */
    public abstract String sign(String[] parm);


    /**
     * 建议的数据签名
     *
     * @return
     */
    public String simpleSignData(long time, String parm) {
        return HashUtil.hash(String.valueOf(time), this.payConf.getSignName(), parm);
    }


}
