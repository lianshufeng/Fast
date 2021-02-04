package com.fast.dev.pay.server.core.hb.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.fast.dev.pay.server.core.hb.dao.HuaXiaEnterpriseAccountDao;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaEnterpriseAutoChargeUserOrderDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAutoChargeUserOrder;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseAutoChargeUserOrderModel;
import net.bytebuddy.asm.Advice;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class HuaXiaEnterpriseAutoChargeUserOrderDaoImpl implements HuaXiaEnterpriseAutoChargeUserOrderDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private HuaXiaEnterpriseAccountDao huaXiaEnterpriseAccountDao;

    @Override
    public Page<HuaXiaEnterpriseAutoChargeUserOrder> list(HuaXiaEnterpriseAutoChargeUserOrderModel model, Pageable pageable) {

        Criteria criteria = new Criteria();
        criteria = EntityObjectUtil.buildCriteria(criteria, model, EntityObjectUtil.CriteriaType.Like, "consumePhone");
        criteria = criteria.and("huaXiaEnterpriseAccount").is(this.huaXiaEnterpriseAccountDao.findByEpId(model.getEpId()));

        //过滤是否已使用的数据
        if (model.getUsed() != null) {
            criteria = criteria.and("used").is(model.getUsed());
        }

        return this.dbHelper.pages(Query.query(criteria), pageable, HuaXiaEnterpriseAutoChargeUserOrder.class);
    }

    @Override
    public boolean setUserOrderUsed(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("used", true);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query, update, HuaXiaEnterpriseAutoChargeUserOrder.class).getModifiedCount() > 0;
    }
}
