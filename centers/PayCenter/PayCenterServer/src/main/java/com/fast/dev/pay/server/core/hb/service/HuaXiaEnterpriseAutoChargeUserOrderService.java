package com.fast.dev.pay.server.core.hb.service;

import com.fast.dev.data.base.util.PageEntityUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.server.core.hb.conf.HuaxiaConf;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAccountDao;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAutoChargeUserOrderDao;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeUserOrder;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAutoChargeUserOrderModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class HuaXiaEnterpriseAutoChargeUserOrderService {


    @Autowired
    private HuaXiaEnterpriseAutoChargeUserOrderDao huaXiaEnterpriseAutoChargeUserOrderDao;

    @Autowired
    private HuaXiaEnterpriseAccountDao huaXiaEnterpriseAccountDao;

    @Autowired
    private HuaxiaConf huaxiaConf;

    @Autowired
    private DBHelper dbHelper;


    @Transactional
    public ResultContent add(String epId, HuaXiaEnterpriseAutoChargeUserOrderModel userOrderModel) {

        //企业账户
        HuaXiaEnterpriseAccount account = huaXiaEnterpriseAccountDao.findByEpId(epId);
        Assert.notNull(account, "企业id不正确");

        HuaXiaEnterpriseAutoChargeUserOrder huaXiaEnterpriseAutoChargeUserOrder = new HuaXiaEnterpriseAutoChargeUserOrder();
        BeanUtils.copyProperties(userOrderModel, huaXiaEnterpriseAutoChargeUserOrder, "epId", "hbId", "hbCode", "used", "endTime");
        huaXiaEnterpriseAutoChargeUserOrder.setHuaXiaEnterpriseAccount(account);

        //设置到期时间
        huaXiaEnterpriseAutoChargeUserOrder.setEndTime(this.dbHelper.getTime() + huaxiaConf.getUserOrderLimitTime());

        this.huaXiaEnterpriseAutoChargeUserOrderDao.save(huaXiaEnterpriseAutoChargeUserOrder);
        return ResultContent.buildContent(huaXiaEnterpriseAutoChargeUserOrder.getId());
    }

    /**
     * 读取用户订单
     *
     * @param id
     * @return
     */
    public ResultContent<HuaXiaEnterpriseAutoChargeUserOrderModel> readOrder(String id) {
        HuaXiaEnterpriseAutoChargeUserOrder userOrder = this.huaXiaEnterpriseAutoChargeUserOrderDao.findTop1ById(id);
        ResultState state = validateUserOrder(userOrder);
        if (state != null) {
            return ResultContent.build(state);
        }
        return ResultContent.buildContent(toModel(userOrder));
    }


    /**
     * 条件查询页面
     *
     * @param model
     * @param pageable
     * @return
     */
    @Transactional
    public Page<HuaXiaEnterpriseAutoChargeUserOrderModel> list(HuaXiaEnterpriseAutoChargeUserOrderModel model, Pageable pageable) {
        return PageEntityUtil.concurrent2PageModel(huaXiaEnterpriseAutoChargeUserOrderDao.list(model, pageable), (huaXiaEnterpriseAutoChargeUserOrder) -> {
            return toModel(huaXiaEnterpriseAutoChargeUserOrder);
        });
    }

    /**
     * 设置用户订单为使用
     *
     * @param id
     */
    @Transactional
    public boolean setUserOrderUsed(String id) {
        return this.huaXiaEnterpriseAutoChargeUserOrderDao.setUserOrderUsed(id);
    }


    /**
     * 检查用户订单是否可用
     *
     * @return
     */
    public ResultState validateUserOrder(String orderId) {
        HuaXiaEnterpriseAutoChargeUserOrder userOrder = this.huaXiaEnterpriseAutoChargeUserOrderDao.findTop1ById(orderId);
        return validateUserOrder(userOrder);
    }


    /**
     * 检查用户订单是否可用
     *
     * @param userOrder
     * @return
     */
    private ResultState validateUserOrder(HuaXiaEnterpriseAutoChargeUserOrder userOrder) {
        //订单不存在
        if (userOrder == null) {
            return ResultState.OrderNotExist;
        }

        //用户订单已超时
        long nowTime = this.dbHelper.getTime();
        if (nowTime > userOrder.getEndTime()) {
            return ResultState.OrderNotValidTime;
        }

        //订单已使用
        if (userOrder.isUsed()) {
            return ResultState.OrderIsUsed;
        }


        return null;
    }


    /**
     * 转换到模型
     *
     * @param huaXiaEnterpriseAutoChargeUserOrder
     * @return
     */
    HuaXiaEnterpriseAutoChargeUserOrderModel toModel(HuaXiaEnterpriseAutoChargeUserOrder huaXiaEnterpriseAutoChargeUserOrder) {
        HuaXiaEnterpriseAutoChargeUserOrderModel model = new HuaXiaEnterpriseAutoChargeUserOrderModel();
        BeanUtils.copyProperties(huaXiaEnterpriseAutoChargeUserOrder, model);

        // 是否使用过
        model.setUsed(huaXiaEnterpriseAutoChargeUserOrder.isUsed());

        //是否已过期
        model.setTimeOut(this.dbHelper.getTime() > huaXiaEnterpriseAutoChargeUserOrder.getEndTime());

        // 企业账户
        HuaXiaEnterpriseAccount account = huaXiaEnterpriseAutoChargeUserOrder.getHuaXiaEnterpriseAccount();
        if (account != null) {
            model.setEpId(account.getEpId());
            model.setHbId(account.getId());
            model.setHbCode(account.getCode());
            //企业工作时间
            model.setEpWorkTime(account.getWorkTime());
            //企业名称
            model.setEpName(account.getEnterpriseName());
            //企业的客服电话
            model.setCustomerPhone(account.getCustomerPhone());
        }


        return model;
    }


}
