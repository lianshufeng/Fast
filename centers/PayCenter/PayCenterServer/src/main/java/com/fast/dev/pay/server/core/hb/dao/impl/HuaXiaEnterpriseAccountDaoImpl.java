package com.fast.dev.pay.server.core.hb.dao.impl;

import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.auth.client.service.EnterpriseService;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.fast.dev.pay.client.service.EnterprisePayAccountService;
import com.fast.dev.pay.server.core.general.domain.EnterpriseAccount;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaEnterpriseAccountDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.helper.TimeHelper;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAccountModel;
import com.fast.dev.pay.server.core.hb.service.HuaXiaEnterpriseAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

public class HuaXiaEnterpriseAccountDaoImpl implements HuaXiaEnterpriseAccountDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private TimeHelper timeFormatHelper;


    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private HuaXiaEnterpriseAccountService huaXiaEnterpriseAccountService;

    @Override
    public Page<HuaXiaEnterpriseAccount> list(HuaXiaEnterpriseAccountModel huaXiaEnterpriseAccountModel, Pageable pageable) {
        Criteria criteria = new Criteria();
        criteria = EntityObjectUtil.buildCriteria(criteria, huaXiaEnterpriseAccountModel, EntityObjectUtil.CriteriaType.Like, "enterpriseName", "enterprisePhone", "enterprisePersonName", "mchtId", "mchtNo", "appid", "salePhone");
        return this.dbHelper.pages(Query.query(criteria), pageable, HuaXiaEnterpriseAccount.class);
    }

    @Override
    public List<HuaXiaEnterpriseAccount> findWorkEnterprise(int max, int workTime) {
        //会话
        String session = UUID.randomUUID().toString();
        long[] today = this.timeFormatHelper.getTodayTime();


        BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, HuaXiaEnterpriseAccount.class);
        for (int i = 0; i < max; i++) {

            Criteria criteria = new Criteria();
            criteria = criteria.and("epId").exists(true);
            criteria = criteria.and("workTime").lte(workTime);
            //过滤在此区间
            criteria = criteria.orOperator(
                    Criteria.where("taskProcess.workTime").exists(false),
                    Criteria.where("taskProcess.workTime").lt(today[0]),
                    Criteria.where("taskProcess.workTime").gt(today[1])
            );

            Query query = Query.query(criteria);
            Update update = new Update();
            update.set("taskProcess.session", session);
            update.set("taskProcess.workTime", this.dbHelper.getTime());
            bulkOperations.updateOne(query, update);
        }
        bulkOperations.execute().getModifiedCount();


        Query query = Query.query(Criteria.where("taskProcess.session").is(session));
        return this.mongoTemplate.find(query, HuaXiaEnterpriseAccount.class);
    }

    @Override
    public HuaXiaEnterpriseAccount findByEpId(String epId) {
        if (!StringUtils.hasText(epId)) {
            return null;
        }

        Query query = Query.query(Criteria.where("epId").is(epId));
        HuaXiaEnterpriseAccount huaXiaEnterpriseAccount = this.mongoTemplate.findOne(query, HuaXiaEnterpriseAccount.class);
        if (huaXiaEnterpriseAccount != null) {
            return huaXiaEnterpriseAccount;
        }


        return addHuaXiaEnterpriseAccount(epId);
    }

    /**
     * 添加华夏企业账户
     *
     * @param epId
     * @return
     */
    private HuaXiaEnterpriseAccount addHuaXiaEnterpriseAccount(String epId) {

        //查询企业
        EnterpriseModel enterpriseModel = enterpriseService.get(epId);
        if (enterpriseModel == null || !StringUtils.hasText(enterpriseModel.getId())) {
            return null;
        }

        //创建企业
        Query query = Query.query(Criteria.where("epId").is(epId));
        Update update = new Update();
        update.setOnInsert("epId", epId);
        update.setOnInsert("enterpriseName", enterpriseModel.getName());
        this.dbHelper.saveTime(update);
        HuaXiaEnterpriseAccount[] huaXiaEnterpriseAccount = new HuaXiaEnterpriseAccount[1];

        try {
            huaXiaEnterpriseAccount[0] = this.mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true), HuaXiaEnterpriseAccount.class);
        } catch (DuplicateKeyException e) {
            return addHuaXiaEnterpriseAccount(epId);
        }


        //初始化华夏企业信息
        this.dbHelper.noTransaction(() -> {
            HuaXiaEnterpriseAccountModel huaXiaEnterpriseAccountModel = new HuaXiaEnterpriseAccountModel();
            huaXiaEnterpriseAccountModel.setId(huaXiaEnterpriseAccount[0].getId());
            this.huaXiaEnterpriseAccountService.update(huaXiaEnterpriseAccountModel).getContent();
        });

        return this.mongoTemplate.findOne(Query.query(Criteria.where("_id").is(huaXiaEnterpriseAccount[0].getId())), HuaXiaEnterpriseAccount.class);
    }


}

