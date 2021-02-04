package com.fast.dev.pay.server.core.hb.controller.user;

import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAccountDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAutoChargeContractDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAutoChargeTemplateDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAutoChargeUserOrderDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeTemplate;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeUserOrder;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAutoChargeContractModel;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseUserOpenAccountInfoModel;
import com.fast.dev.pay.server.core.hb.model.SuperAutoChargeContractModel;
import com.fast.dev.pay.server.core.hb.model.SuperAutoChargeModel;
import com.fast.dev.pay.server.core.hb.model.req.OpenAcctReq;
import com.fast.dev.pay.server.core.hb.service.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hb/ea/user")
public class HuaXiaEnterpriseAutoChargeContractController {

    @Autowired
    private HuaXiaEnterpriseAutoChargeUserOrderDao huaXiaEnterpriseAutoChargeUserOrderDao;

    @Autowired
    private HuaXiaEnterpriseAutoChargeContractService huaXiaEnterpriseAutoChargeContractService;

    @Autowired
    private HuaXiaEnterpriseAutoChargeTemplateDao huaXiaEnterpriseAutoChargeTemplateDao;

    @Autowired
    private HuaXiaEnterpriseAccountDao huaXiaEnterpriseAccountDao;

    @Autowired
    private HuaXiaEnterpriseAutoChargeTemplateService huaXiaEnterpriseAutoChargeTemplateService;

    @Autowired
    private HuaXiaEnterpriseAutoChargeUserOrderService huaXiaEnterpriseAutoChargeUserOrderService;


    @Autowired
    private HuaXiaEnterpriseAutoChargeContractDao huaXiaEnterpriseAutoChargeContractDao;

    @Autowired
    private HuaXiaApiService huaXiaApiService;


    /**
     * 检查用户是否已经开过户
     *
     * @param phone
     * @return
     */
    @UserLog
    @RequestMapping("checkOpenAccount")
    public Object checkOpenAccount(String phone) {
        Assert.hasText(phone, "手机号不能为空");
        return huaXiaEnterpriseAutoChargeContractService.checkOpenAccount(phone);
    }

    /**
     * 发送开户短信验证码
     *
     * @param hbcode
     * @return
     */
    @UserLog
    @RequestMapping("sendOpenAccountMessageNo")
    public Object sendOpenAccountMessageNo(String hbcode, String phone,String orderId) {
        Assert.hasText(phone, "开户手机不能为空");
        HuaXiaEnterpriseAccount huaXiaEnterpriseAccount = null;
        if (!StringUtils.isEmpty(hbcode)){
            huaXiaEnterpriseAccount = huaXiaEnterpriseAccountDao.findByCode(hbcode);
        }
        if (!StringUtils.isEmpty(orderId)){
            huaXiaEnterpriseAccount = huaXiaEnterpriseAutoChargeUserOrderDao.findTop1ById(orderId).getHuaXiaEnterpriseAccount();
        }
        Assert.notNull(huaXiaEnterpriseAccount, "企业编码错误");
        return ResultContent.buildContent(huaXiaApiService.sendMessageNo(huaXiaEnterpriseAccount, phone));
    }

    /**
     * 发送绑定短信验证码
     *
     * @param phone
     * @return
     */
    @UserLog
    @RequestMapping("sendBindAccountMessageNo")
    public Object sendBindAccountMessageNo(String phone) {
        Assert.hasText(phone, "开户手机不能为空");
        return huaXiaEnterpriseAutoChargeContractService.sendBindMessageNo(phone);
    }

    /**
     * 添加套餐合同
     *
     * @param model
     * @return
     */
    @RequestMapping("openAccountFromTemplate")
    public Object openAccountFromTemplate(@RequestBody TemplateOrderModel model) {
        SuperAutoChargeModel autoCharge = new SuperAutoChargeModel();
        Object ret = preFromTemplate(model, autoCharge);
        if (ret != null) {
            return ret;
        }
        return huaXiaEnterpriseAutoChargeContractService.openAccount(model, autoCharge);
    }

    /**
     * 绑定套餐合同
     *
     * @param model
     * @return
     */
    @RequestMapping("bindAccountFromTemplate")
    public Object bindAccountFromTemplate(@RequestBody TemplateOrderModel model) {
        if (!huaXiaEnterpriseAutoChargeContractService.validateMessageNo(model.getSessionId(), model.getMessageNo())) {
            return ResultContent.build(ResultState.validateMessageFail);
        }
        // 扣款信息
        SuperAutoChargeModel autoCharge = new SuperAutoChargeModel();
        Object ret = preFromTemplate(model, autoCharge);
        if (ret != null) {
            return ret;
        }
        return huaXiaEnterpriseAutoChargeContractService.insertContract(model, autoCharge);
    }


    @SneakyThrows
    public Object preFromTemplate(TemplateOrderModel model, SuperAutoChargeModel autoCharge) {
        Assert.hasText(model.getConsumePhone(), "消费者手机号码不能为空");
        HuaXiaEnterpriseAccount huaXiaEnterpriseAccount = huaXiaEnterpriseAccountDao.findByCode(model.getHbCode());
        Assert.notNull(huaXiaEnterpriseAccount, "企业编码错误");
        model.setEpId(huaXiaEnterpriseAccount.getEpId());


        //验证订单合法性
        ResultState state = this.huaXiaEnterpriseAutoChargeTemplateService.validateTemplate(model.getHbCode(), model.getTemplateCode());
        if (state != null) {
            return ResultContent.build(state);
        }

        //模板
        HuaXiaEnterpriseAutoChargeTemplate template = huaXiaEnterpriseAutoChargeTemplateDao.findByHuaXiaEnterpriseAccountAndCode(huaXiaEnterpriseAccount, model.getTemplateCode());
        if (template == null) {
            return ResultContent.build(ResultState.OrderError);
        }

        //设置订单名
        model.setOrderName(template.getName());

        //设置套模板id
        model.setTemplateId(template.getId());

        //校验用户是否已购买过此套餐
        if (huaXiaEnterpriseAutoChargeContractDao.existsUserChargeContract(model.getConsumePhone(), template)) {
            return ResultContent.build(ResultState.OrderRepeat);
        }

        // 扣款信息
        BeanUtils.copyProperties(template, autoCharge);

        return null;
    }


    /**
     * 添加用户订单合同
     *
     * @param model
     * @return
     */
    @RequestMapping("openAccountFromUserOrder")
    public Object openAccountFromUserOrder(@RequestBody UserOrderModel model) {

        // 扣款信息
        SuperAutoChargeModel autoCharge = new SuperAutoChargeModel();
        Object ret = preFromUserOrder(model, autoCharge);
        if (ret != null) {
            return ret;
        }

        ResultContent<HuaXiaEnterpriseUserOpenAccountInfoModel> content = huaXiaEnterpriseAutoChargeContractService.openAccount(model, autoCharge);
        if (content.getState() == ResultState.Success) {
            this.huaXiaEnterpriseAutoChargeUserOrderService.setUserOrderUsed(model.getOrderId());
        }

        return content;
    }


    /**
     * 绑定用户订单合同
     *
     * @param model
     * @return
     */
    @RequestMapping("bindAccountFromUserOrder")
    public Object bindAccountFromUserOrder(@RequestBody UserOrderModel model) {
        if (!huaXiaEnterpriseAutoChargeContractService.validateMessageNo(model.getSessionId(), model.getMessageNo())) {
            return ResultContent.build(ResultState.validateMessageFail);
        }

        // 扣款信息
        SuperAutoChargeModel autoCharge = new SuperAutoChargeModel();
        Object ret = preFromUserOrder(model, autoCharge);
        if (ret != null) {
            return ret;
        }
        ResultContent<HuaXiaEnterpriseUserOpenAccountInfoModel> content = huaXiaEnterpriseAutoChargeContractService.insertContract(model, autoCharge);
        if (content.getState() == ResultState.Success) {
            this.huaXiaEnterpriseAutoChargeUserOrderService.setUserOrderUsed(model.getOrderId());
        }

        return content;
    }


    public Object preFromUserOrder(UserOrderModel model, SuperAutoChargeModel autoCharge) {
        HuaXiaEnterpriseAutoChargeUserOrder userOrder = huaXiaEnterpriseAutoChargeUserOrderDao.findTop1ById(model.getOrderId());
        Assert.notNull(userOrder, "订单id错误");
        model.setEpId(userOrder.getHuaXiaEnterpriseAccount().getEpId());

        //验证订单合法性
        ResultState state = this.huaXiaEnterpriseAutoChargeUserOrderService.validateUserOrder(model.getOrderId());
        if (state != null) {
            return ResultContent.build(state);
        }

        //设置消费者手机号码
        model.setConsumePhone(userOrder.getConsumePhone());

        //设置订单名
        model.setOrderName(userOrder.getName());

        //订单的扣款信息
        BeanUtils.copyProperties(userOrder, autoCharge);

        return null;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TemplateOrderModel extends SuperAutoChargeContractModel {
        private String hbCode;
        private String templateCode;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserOrderModel extends SuperAutoChargeContractModel {
        private String orderId;
    }

}

