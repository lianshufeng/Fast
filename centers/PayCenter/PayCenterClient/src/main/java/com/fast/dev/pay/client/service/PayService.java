package com.fast.dev.pay.client.service;

import com.fast.dev.pay.client.model.*;
import com.fast.dev.pay.client.model.sign.RepSignModel;
import com.fast.dev.pay.client.model.sign.ReqSignModel;
import com.fast.dev.pay.client.result.ResultContent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 支付中心的接口
 */
@FeignClient(name = "paycenterserver/manager/pay")
public interface PayService {


    /**
     * 创建订单
     *
     * @param createPayOrderModel
     */
    @RequestMapping(value = "createOrder")
    ResultContent<PreOrderModel> createOrder(@RequestBody CreatePayOrderModel createPayOrderModel);


    /**
     * 快捷支付
     * @return
     */
    @RequestMapping(value = "pay")
    ResultContent<CallBackResult> pay(@RequestBody CreatePayOrderModel fastPayModel);



    /**
     * 触发订单：
     * 1、如果没有产生回调此方法会与支付平台查询，成功则发广播、
     * 2、如果之前发过广播，订单支付成功也会发广播
     *
     * @param orderId
     * @return
     */
    @RequestMapping(value = "tapOrder")
    ResultContent<CallBackResult> tapOrder(@RequestParam("orderId") String orderId);


    /**
     * 关闭订单
     *
     * @param orderId
     * @return
     */
    @RequestMapping(value = "closeOrder")
    ResultContent<ResponseRefundOrderModel> closeOrder(@RequestParam("orderId") String orderId);


    /**
     * 退款
     *
     * @return
     */
    @RequestMapping(value = "refund")
    ResultContent<ResponseRefundOrderModel> refund(@RequestBody RefundOrderModel refundOrderModel);


    /**
     * 数据签名
     *
     * @param reqSignModel
     * @return
     */
    @RequestMapping(value = "sign")
    ResultContent<RepSignModel> sign(@RequestBody ReqSignModel reqSignModel);


}
