package com.fast.dev.pay.server.core.hb.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAccountDao;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaEnterpriseAutoChargeContractDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeContract;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeTemplate;
import com.fast.dev.pay.server.core.hb.model.ChargeContractModel;
import com.fast.dev.pay.server.core.hb.model.ContractFreezeModel;
import com.fast.dev.pay.server.core.hb.model.ContractRequestModel;
import com.fast.dev.pay.server.core.hb.model.SuperAutoChargeModel;
import com.fast.dev.pay.server.core.hb.type.ChargeState;
import com.fast.dev.pay.server.core.hb.type.ContractFreezeState;
import com.fast.dev.pay.server.core.hb.type.ContractState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class HuaXiaEnterpriseAutoChargeContractDaoImpl implements HuaXiaEnterpriseAutoChargeContractDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private HuaXiaEnterpriseAccountDao huaXiaEnterpriseAccountDao;

    @Override
    public boolean updateFreeze(String id, ContractFreezeModel contractFreeze) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("contractFreeze", contractFreeze);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query, update, HuaXiaEnterpriseAutoChargeContract.class).getModifiedCount() > 0;
    }

    @Override
    public boolean updateState(String id, ContractState state) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("contractState", state);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query, update, HuaXiaEnterpriseAutoChargeContract.class).getModifiedCount() > 0;
    }

    @Override
    public boolean updateLastChargeContract(String id, ChargeContractModel contractModel) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("lastChargeContract", contractModel);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query, update, HuaXiaEnterpriseAutoChargeContract.class).getModifiedCount() > 0;
    }

    @Override
    public boolean updateAutoCharge(String id, SuperAutoChargeModel autoChargeModel) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("autoCharge", autoChargeModel);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query, update, HuaXiaEnterpriseAutoChargeContract.class).getModifiedCount() > 0;
    }

    @Override
    public Page<HuaXiaEnterpriseAutoChargeContract> list(String epId, ContractRequestModel requestModel, Pageable pageable) {
        //企业
        Criteria criteria = Criteria.where("huaXiaEnterpriseAccount").is(this.huaXiaEnterpriseAccountDao.findByEpId(epId));

        //组合条件
        List<Criteria> ands = new ArrayList<>();


        //扣款人姓名
        if (StringUtils.hasText(requestModel.getUserName())) {
            ands.add(Criteria.where("userAccount.userName").regex(requestModel.getUserName()));
        }

        //扣款人电话
        if (StringUtils.hasText(requestModel.getUserPhone())) {
            ands.add(Criteria.where("userAccount.userMobile").regex(requestModel.getUserPhone()));
        }

        //消费者电话
        if (StringUtils.hasText(requestModel.getConsumePhone())) {
            ands.add(Criteria.where("consumePhone").regex(requestModel.getConsumePhone()));
        }

        //订单状态
        if (requestModel.getOrderState() != null) {
            List<Criteria> criteriaList = new ArrayList<>();
            for (ContractState contractState : requestModel.getOrderState().getState()) {
                criteriaList.add(Criteria.where("contractState").is(contractState));
            }
            ands.add(new Criteria().orOperator(criteriaList.toArray(new Criteria[0])));

        }

        //扣款状态,没有扣款状态也算正常
        if (requestModel.getChargeState() != null) {
            List<Criteria> criteriaOr = new ArrayList<>();
            for (ChargeState chargeState : requestModel.getChargeState().getState()) {
                criteriaOr.add(Criteria.where("lastChargeContract.state").is(chargeState));
            }
            if (requestModel.getChargeState() == ContractRequestModel.ChargeState.Success) {
                criteriaOr.add(Criteria.where("lastChargeContract.state").exists(false));
            }
            ands.add(new Criteria().orOperator(criteriaOr.toArray(new Criteria[0])));
        }


        //冻结状态,没有扣款状态也算正常
        if (requestModel.getFreezeState() != null) {
            List<Criteria> criteriaOr = new ArrayList<>();
            for (ContractFreezeState freezeState : requestModel.getFreezeState().getState()) {
                criteriaOr.add(Criteria.where("contractFreeze.state").is(freezeState));
            }
            if (requestModel.getFreezeState() == ContractRequestModel.FreezeState.Success) {
                criteriaOr.add(Criteria.where("contractFreeze.state").exists(false));
            }
            ands.add(new Criteria().orOperator(criteriaOr.toArray(new Criteria[0])));
        }


        //冻结时间
        if (requestModel.getStartFreezeTime() != null && requestModel.getEndFreezeTime() != null) {
            ands.add(new Criteria().andOperator(
                    Criteria.where("contractFreeze.time").exists(true),
                    Criteria.where("contractFreeze.time").gte(requestModel.getStartFreezeTime()),
                    Criteria.where("contractFreeze.time").lte(requestModel.getEndFreezeTime())
            ));
        }


        //扣款时间
        if (requestModel.getStartChargeTime() != null && requestModel.getEndChargeTime() != null) {
            ands.add(new Criteria().andOperator(
                    Criteria.where("lastChargeContract.time").exists(true),
                    Criteria.where("lastChargeContract.time").gte(requestModel.getStartChargeTime()),
                    Criteria.where("lastChargeContract.time").lte(requestModel.getEndChargeTime())
            ));
        }


        if (ands.size() > 0) {
            criteria.andOperator(ands.toArray(new Criteria[0]));
        }

        return this.dbHelper.pages(Query.query(criteria), pageable, HuaXiaEnterpriseAutoChargeContract.class);
    }


    @Override
    public boolean existsUserChargeContract(String consumePhone, HuaXiaEnterpriseAutoChargeTemplate template) {
        Criteria criteria = new Criteria();
        criteria = criteria.and("huaXiaEnterpriseAccount").is(template.getHuaXiaEnterpriseAccount());
        criteria = criteria.and("consumePhone").is(consumePhone);
        criteria = criteria.and("template").is(template);

        //正在扣款的状态
        criteria = criteria.orOperator(
                Criteria.where("contractState").is(ContractState.WaitCharge),
                Criteria.where("contractState").is(ContractState.WaitFreeze)
        );
        return this.mongoTemplate.exists(Query.query(criteria), HuaXiaEnterpriseAutoChargeContract.class);
    }

    @Override
    public long countContract(Long startTime, Long endTime,String epId) {

        Query query = new Query();
        Criteria criteria = new Criteria();

        if (startTime != null && endTime != null){
            criteria.and("createTime").gte(startTime).lte(endTime);
        } else if (startTime != null && endTime == null){
            criteria.and("createTime").gte(startTime);
        } else if (startTime == null && endTime != null){
            criteria.and("createTime").lte(endTime);
        }

        if (!StringUtils.isEmpty(epId)){
            criteria.and("huaXiaEnterpriseAccount").is(huaXiaEnterpriseAccountDao.findByEpId(epId));
        }
        criteria.and("lastChargeContract").ne(null);

        query.addCriteria(criteria);

        return this.mongoTemplate.count(query,HuaXiaEnterpriseAutoChargeContract.class);
    }

}
