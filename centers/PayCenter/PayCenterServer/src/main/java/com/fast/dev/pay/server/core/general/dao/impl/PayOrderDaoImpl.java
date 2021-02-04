package com.fast.dev.pay.server.core.general.dao.impl;

import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.core.util.encode.HashUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.client.model.PrePayOrderModel;
import com.fast.dev.pay.client.model.RefundModel;
import com.fast.dev.pay.client.type.PayState;
import com.fast.dev.pay.server.core.general.dao.EnterprisePayAccountDao;
import com.fast.dev.pay.server.core.general.dao.extend.PayOrderDaoExtend;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;
import com.fast.dev.pay.server.core.general.domain.PayOrder;
import com.mongodb.client.model.UpdateOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class PayOrderDaoImpl implements PayOrderDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EnterprisePayAccountDao enterprisePayAccountDao;

    @Autowired
    private DBHelper dbHelper;

    @Override
    public boolean existOrder(PrePayOrderModel payOrderModel) {
        return this.mongoTemplate.exists(Query.query(Criteria.where("uniqueIndex").is(createUniqueIndex(payOrderModel.getServiceOrder(), payOrderModel.getPayAccountId()))), PayOrder.class);
    }

    @Override
    public PayOrder findByPreOrder(PrePayOrderModel payOrderModel) {
        return this.mongoTemplate.findOne(Query.query(Criteria.where("uniqueIndex").is(createUniqueIndex(payOrderModel.getServiceOrder(), payOrderModel.getPayAccountId()))), PayOrder.class);
    }

    @Override
    public String createOrder(PrePayOrderModel payOrderModel) {

        PayOrder payOrder = new PayOrder();
        BeanUtils.copyProperties(payOrderModel, payOrder, "state");

        //设置唯一索引
        payOrder.setUniqueIndex(createUniqueIndex(payOrderModel.getServiceOrder(), payOrderModel.getPayAccountId()));

        //企业的支付账号
        payOrder.setEnterprisePayAccount(this.enterprisePayAccountDao.findTop1ById(payOrderModel.getPayAccountId()));

        this.dbHelper.saveTime(payOrder);
        this.mongoTemplate.insert(payOrder);

        return payOrder.getId();
    }

    @Override
    public boolean setSupportPre(String orderId, Map<String, Object> supportPre) {
        Query query = buildQueryFromOrderId(orderId);
        Update update = new Update();
        update.set("supportPre", supportPre);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query, update, PayOrder.class).getModifiedCount() > 0;
    }

    @Override
    public boolean setOrderState(String orderId, PayState state) {
        Query query = buildQueryFromOrderId(orderId);
        Update update = new Update();
        update.set("state", state);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query, update, PayOrder.class).getModifiedCount() > 0;
    }

    @Override
    public boolean setOrderStateAndSupportPost(String orderId, PayState state, Map<String, Object> supportPost) {
        Query query = buildQueryFromOrderId(orderId);
        Update update = new Update();
        update.set("state", state);
        update.set("supportPost", supportPost);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query, update, PayOrder.class).getModifiedCount() > 0;
    }

    @Override
    public PayOrder findPayOrderAndIncBroadCount(String orderId) {
        Query query = buildQueryFromOrderId(orderId);
        Update update = new Update();
        update.inc("broadCount", 1);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), PayOrder.class);
    }

    @Override
    public boolean updateRefund(String orderId, String tradeNo, Object info) {
        //构建查询条件
        Criteria criteria = buildCriteriaFromOrderId(orderId);
        criteria = criteria.and("refund.tradeNo").is(tradeNo);
        final Query query = Query.query(criteria);
        //退款模型
        final RefundModel refundModel = new RefundModel(tradeNo, BeanUtil.toMap(info));
        boolean ret = this.mongoTemplate.updateFirst(query, this.dbHelper.buildUpdate().set("refund.$.other", refundModel), PayOrder.class).getModifiedCount() > 0;
        if (!ret) {
            ret = this.mongoTemplate.updateFirst(buildQueryFromOrderId(orderId), this.dbHelper.buildUpdate().addToSet("refund", refundModel), PayOrder.class).getModifiedCount() == 0;
        }
        return ret;
    }

    @Override
    public boolean closeOrder(String orderId) {
        Query query = buildQueryFromOrderId(orderId);

        Update update = new Update();
        update.set("close", true);
        this.dbHelper.updateTime(update);

        return this.mongoTemplate.updateFirst(query, update, PayOrder.class).getModifiedCount() > 0;
    }


    /**
     * 唯一索引文本
     *
     * @param orderId
     * @param payAccountId
     * @return
     */
    private String createUniqueIndex(String orderId, String payAccountId) {
        return HashUtil.hash(orderId, payAccountId);
    }

    /**
     * 构建通过订单id的查询
     *
     * @param orderId
     * @return
     */
    private static Query buildQueryFromOrderId(String orderId) {
        return Query.query(buildCriteriaFromOrderId(orderId));
    }

    /**
     * 构建查询条件
     *
     * @param orderId
     * @return
     */
    private static Criteria buildCriteriaFromOrderId(String orderId) {
        return Criteria.where("orderId").is(orderId);
    }
}
