package com.fast.dev.pay.server.core.general.service.impl;

import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.client.model.*;
import com.fast.dev.pay.client.model.sign.RepSignModel;
import com.fast.dev.pay.client.model.sign.ReqSignModel;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.client.service.PayService;
import com.fast.dev.pay.client.support.BasePaySupport;
import com.fast.dev.pay.client.type.AccountType;
import com.fast.dev.pay.client.type.PayState;
import com.fast.dev.pay.client.util.OrderUtil;
import com.fast.dev.pay.server.core.cpcn.param.PayModel;
import com.fast.dev.pay.server.core.general.dao.EnterprisePayAccountDao;
import com.fast.dev.pay.server.core.general.dao.PayOrderDao;
import com.fast.dev.pay.server.core.general.dao.UserBankCardDao;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;
import com.fast.dev.pay.server.core.general.domain.PayOrder;
import com.fast.dev.pay.server.core.general.domain.UserBankCard;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelper;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelperCacheManager;
import com.fast.dev.pay.server.core.general.helper.cpcn.CpcnHelper;
import com.fast.dev.pay.server.core.general.service.OrderBroadcastService;
import com.fast.dev.pay.server.core.general.service.callback.SuperCallBackManagerService;
import com.fast.dev.pay.server.core.general.service.pay.PaySupportServiceManager;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class PayServiceImpl implements PayService {


    @Autowired
    private OrderBroadcastService orderBroadcastService;

    @Autowired
    private SuperCallBackManagerService superCallBackManagerService;

    @Autowired
    private PaySupportServiceManager paySupportServiceManager;

    @Autowired
    private PaySupportHelperCacheManager paySupportHelperCacheManager;

    @Autowired
    private PayOrderDao payOrderDao;

    @Autowired
    private EnterprisePayAccountDao enterprisePayAccountDao;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private UserBankCardDao userBankCardDao;


    /**
     * 获取订单详情
     *
     * @param orderId
     * @return
     */
    public PayOrder getLocalOrder(String orderId) {
        return this.payOrderDao.findByOrderId(orderId);
    }


    @Override
    @Transactional
    @UserLog(parameter = "#payOrderModel")
    public ResultContent<PreOrderModel> createOrder(CreatePayOrderModel payOrderModel) {

        //校验订单
        validateParam(payOrderModel);

        //将创建订单转换为与支付订单
        PrePayOrderModel prePayOrderModel = transformPaySupportParameter(payOrderModel);
        final String orderId = prePayOrderModel.getOrderId();

        //企业支付账号
        EnterprisePayAccount enterprisePayAccount = this.enterprisePayAccountDao.findTop1ById(payOrderModel.getPayAccountId());

        //校验支付账户和订单
        ResultContent<PreOrderModel> validateResult = validateEpAccountAndOrder(enterprisePayAccount, prePayOrderModel);
        if (validateResult != null) {
            return validateResult;
        }

        //本地创建数据
        PayOrder order = this.payOrderDao.createOrder(prePayOrderModel);

        //执行创建订单业务
        ResultContent<PreOrderModel> resultContent = this.paySupportServiceManager.execute(prePayOrderModel);

        //更新本地订单状态
        if (resultContent.getState() == ResultState.Success) {
            this.payOrderDao.setOrderState(orderId, PayState.PrePay);
            this.payOrderDao.setSupportPre(orderId, resultContent.getContent().getSupport());
        } else {
            //数据库事务回滚
            this.dbHelper.rollbackTransaction();
//            this.payOrderDao.removeById(preOrderId);

            this.payOrderDao.setOrderState(orderId, PayState.Cancel);
            this.payOrderDao.setSupportPre(orderId, resultContent.getContent().getError());
        }


        return resultContent;
    }


    @Override
    @UserLog(parameter = "#fastPayModel")
    public ResultContent<CallBackResult> pay(CreatePayOrderModel fastPayModel) {

        Assert.notNull(fastPayModel.getParm(), "扩展参数不能为空");
        Assert.notNull(fastPayModel.getParm().get("userCardId"), "用户绑定卡ID不能为空");
        Assert.notNull(fastPayModel.getParm().get("splitResult"), "分账结果不能为空");
        final String userCardId = String.valueOf(fastPayModel.getParm().get("userCardId"));
        final String splitResult = String.valueOf(fastPayModel.getParm().get("splitResult"));


//        validateParam(fastPayModel);
//        //将创建订单转换为与支付订单
//        PrePayOrderModel prePayOrderModel = transformPaySupportParameter(fastPayModel);
//        final String orderId = prePayOrderModel.getOrderId();
//
//        //企业支付账号
//        EnterprisePayAccount enterprisePayAccount = this.enterprisePayAccountDao.findTop1ById(fastPayModel.getPayAccountId());
//
//        //校验支付账户和订单
//        ResultContent<PreOrderModel> validateResult = validateEpAccountAndOrder(enterprisePayAccount, prePayOrderModel);
//        if (validateResult != null){
//            return validateResult;
//        }
//
//        UserBankCard userBankCard = this.userBankCardDao.findTop1ById(fastPayModel.getUserCardId());
//        if (userBankCard == null) {
//            return  ResultContent.build(ResultState.UserBankCardNotExist);
//        }

        //创建预支付订单
        ResultContent<PreOrderModel> orderContent = createOrder(fastPayModel);

        if (orderContent.getState() != ResultState.Success){
            return ResultContent.build(ResultState.OrderError);
        }

        //获取支付账号
        EnterprisePayAccount enterprisePayAccount = this.enterprisePayAccountDao.findTop1ById(fastPayModel.getPayAccountId());

        //获取用户绑卡信息
        UserBankCard userBankCard = this.userBankCardDao.findTop1ById(userCardId);

        //查询订单
        PayOrder payOrder = this.payOrderDao.findByOrderId(orderContent.getContent().getPlatformOrder());

        final CpcnHelper cpcnHelper = paySupportHelperCacheManager.get(enterprisePayAccount, CpcnHelper.class);

        Map<String, Object> orderItem = cpcnHelper.payment(payOrder, PayModel.builder().txSNBinding(userBankCard.getTxSNBinding()).splitResult(splitResult).build());
        if (orderItem == null) {
            return ResultContent.build(ResultState.PaySupporOrderQueryError);
        }
        //回调数据
        CallBackResult result = this.superCallBackManagerService.get(fastPayModel.getMethod().getAccountType()).callback(payOrder, orderItem);
        if (result.getState() == PayState.Paid){
            return ResultContent.build(ResultState.Success,result);
        }
        switch (result.getState()){
            case Paid:
                return ResultContent.build(ResultState.Success,result);
            case PrePay:
                return ResultContent.build(ResultState.OrderProcess,result);
            default :
                return ResultContent.build(ResultState.Fail,result,result.getError());
        }
    }


    /**
     * 校验订单
     */
    private void validateParam(CreatePayOrderModel createPayOrderModel) {
        Assert.hasText(createPayOrderModel.getServiceOrder(), "业务订单不能为空");
        Assert.notNull(createPayOrderModel.getMethod(), "支付方式不能为空");
        Assert.notNull(createPayOrderModel.getPrice(), "订单金额不能为空");
        Assert.notNull(createPayOrderModel.getProduct(), "商品信息不能为空");
        Assert.state(createPayOrderModel.getPrice() >= 0, "订单金额必须大于或者等于0");
        Assert.hasText(createPayOrderModel.getPayAccountId(), "企业的支付账号的id不能为空");
        Assert.isTrue(createPayOrderModel.getServiceCode().length() == 2, "业务编码长度必须为2位");

    }

    /**
     * 校验支付账户和订单
     *
     * @param enterprisePayAccount
     * @param prePayOrderModel
     * @return
     */
    ResultContent<PreOrderModel> validateEpAccountAndOrder(EnterprisePayAccount enterprisePayAccount, PrePayOrderModel prePayOrderModel) {
        //企业支付账号不存在
        if (enterprisePayAccount == null) {
            return ResultContent.build(ResultState.EnterprisePayAccountNotExist);
        }

        //企业支付账号被禁用
        if (enterprisePayAccount.isDisable()) {
            return ResultContent.build(ResultState.EnterprisePayAccountDisable);
        }

        //检验订单已存在
        if (this.payOrderDao.existOrder(prePayOrderModel)) {
            return ResultContent.build(ResultState.OrderExist, readPreOrder(prePayOrderModel));
        }

        return null;
    }

    /**
     * 读取预付费订单
     *
     * @return
     */
    private PreOrderModel readPreOrder(PrePayOrderModel prePayOrderModel) {
        PayOrder payOrder = this.payOrderDao.findByPreOrder(prePayOrderModel);
        if (payOrder == null) {
            return null;
        }


        PreOrderModel preOrderModel = new PreOrderModel();
        preOrderModel.setPlatformOrder(payOrder.getOrderId());
        preOrderModel.setSupport(payOrder.getSupportPre());

        return preOrderModel;
    }

    @Override
//    @Transactional
    public ResultContent<CallBackResult> tapOrder(final String orderId) {
        //查询订单
        PayOrder payOrder = this.payOrderDao.findByOrderId(orderId);
        if (payOrder == null) {
            return ResultContent.build(ResultState.OrderNotExist);
        }

        //企业支付账号的类型
        final AccountType accountType = payOrder.getMethod().getAccountType();
        // 三方支付助手
        final PaySupportHelper paySupportHelper = this.paySupportHelperCacheManager.get(payOrder.getEnterprisePayAccount(), accountType);
        //调用第三方查询
        Map<String, Object> orderItem = paySupportHelper.queryOrder(payOrder);
        if (orderItem == null) {
            return ResultContent.build(ResultState.PaySupporOrderQueryError);
        }

        //回调数据
        CallBackResult result = this.superCallBackManagerService.get(accountType).callback(payOrder, orderItem);
        return ResultContent.build(result.getState()==PayState.Paid?ResultState.Success:ResultState.Fail,result);
    }

    @Override
    @Transactional
    @UserLog(action = "closeOrder",parameter = "#orderId")
    public ResultContent<ResponseRefundOrderModel> closeOrder(String orderId) {
        //查询订单
        PayOrder payOrder = this.payOrderDao.findByOrderId(orderId);
        if (payOrder == null) {
            return ResultContent.build(ResultState.OrderNotExist);
        }

        //查询第三方支付该比订单，是否已支付成功
        final AccountType accountType = payOrder.getMethod().getAccountType();
        final PaySupportHelper paySupportHelper = this.paySupportHelperCacheManager.get(payOrder.getEnterprisePayAccount(), accountType);
        Map<String, Object> orderItem = paySupportHelper.queryOrder(payOrder);
        PayState payState = this.superCallBackManagerService.get(accountType).getPayState(payOrder, orderItem);
        if (payState == PayState.Paid) {
            return ResultContent.build(ResultState.OrderPaid);
        }


        //数据库中关闭订单
        this.payOrderDao.closeOrder(orderId);

        return ResultContent.buildContent(paySupportHelper.closeOrder(payOrder));
    }


    @Override
//    @Transactional
    @UserLog(action = "refund",parameter = "#refundOrderModel")
    public ResultContent<ResponseRefundOrderModel> refund(RefundOrderModel refundOrderModel) {

        //订单号
        final String orderId = refundOrderModel.getOrderId();

        //查询订单
        PayOrder payOrder = this.payOrderDao.findByOrderId(orderId);
        if (payOrder == null) {
            return ResultContent.build(ResultState.OrderNotExist);
        }

        //如果没有退款单号则自动生成一个
        if (!StringUtils.hasText(refundOrderModel.getRefundNo())) {
            refundOrderModel.setRefundNo(OrderUtil.build(payOrder.getEnterprisePayAccount().getAccountType(), "tk"));
        }

        //企业支付账号的类型
        final AccountType accountType = payOrder.getMethod().getAccountType();
        // 三方支付助手
        final PaySupportHelper paySupportHelper = this.paySupportHelperCacheManager.get(payOrder.getEnterprisePayAccount(), accountType);
        //调用退款流程
        ResponseRefundOrderModel refund = paySupportHelper.refund(payOrder, refundOrderModel);

        //写入DB
        if (refund.isSuccess()) {
            this.payOrderDao.updateRefund(refundOrderModel.getOrderId(), refund.getTradeNo(), refund.getOther(),refundOrderModel.getAmount());

        }
        return ResultContent.build(refund.isSuccess() ? ResultState.Success : ResultState.RefundError, refund);
    }

    @Override
    public ResultContent<RepSignModel> sign(ReqSignModel reqSignModel) {
        Assert.hasText(reqSignModel.getPayAccountId(), "支付账号id不能为空");
        EnterprisePayAccount enterprisePayAccount = this.enterprisePayAccountDao.findTop1ById(reqSignModel.getPayAccountId());
        Assert.notNull(enterprisePayAccount, "支付账户不存在");

        //企业支付账号的类型
        final AccountType accountType = enterprisePayAccount.getAccountType();

        //  三方支付助手
        final PaySupportHelper paySupportHelper = this.paySupportHelperCacheManager.get(enterprisePayAccount, accountType);

        return ResultContent.buildContent(RepSignModel.builder().sign(paySupportHelper.sign(reqSignModel.getParm())).build());
    }

    /**
     * 创建支付对象
     *
     * @param payOrderModel
     * @return
     */
    @SneakyThrows
    private PrePayOrderModel transformPaySupportParameter(CreatePayOrderModel payOrderModel) {

        PrePayOrderModel prePayOrderModel = new PrePayOrderModel();
        BeanUtils.copyProperties(payOrderModel, prePayOrderModel);

        Class<? extends BasePaySupport> basePaySupportCls = payOrderModel.getMethod().getSupport();
        if (basePaySupportCls == null) {
            return prePayOrderModel;
        }

        //构建第三方支付的参数
        BasePaySupport basePaySupport = null;
        //设置扩展参数,有自定义参数则通过json实例化对象
        if (payOrderModel.getParm() != null && payOrderModel.getParm().size() > 0) {
            basePaySupport = JsonUtil.toObject(JsonUtil.toJson(payOrderModel.getParm()), payOrderModel.getMethod().getSupport());
        } else {
            basePaySupport = BeanUtil.newClass(basePaySupportCls);
        }


        prePayOrderModel.setBasePaySupport(basePaySupport);
        prePayOrderModel.setState(PayState.PrePay);

        //构建订单
        paySupportServiceManager.getPaySupportService(payOrderModel.getMethod()).ifPresent((it) -> {
            prePayOrderModel.setOrderId(it.createOrderId(payOrderModel.getServiceCode()));
        });


        return prePayOrderModel;

    }


    /**
     * 转换到模型
     *
     * @param payOrder
     * @return
     */
    public PayOrderModel toModel(PayOrder payOrder) {
        if (payOrder == null) {
            return null;
        }
        PayOrderModel payOrderModel = new PayOrderModel();
        BeanUtils.copyProperties(payOrder, payOrderModel);

        payOrderModel.setPayAccountId(payOrder.getEnterprisePayAccount().getId());
        return payOrderModel;
    }


}
