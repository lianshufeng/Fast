package com.fast.dev.auth.center.server.core.dao.impl;

import com.fast.dev.auth.center.server.core.dao.extend.EnterpriseDaoExtend;
import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.helper.ReIndexHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class EnterpriseDaoImpl implements EnterpriseDaoExtend {

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ReIndexHelper reIndexHelper;

    @Override
    public Page<Enterprise> list(EnterpriseModel enterpriseModel, Pageable pageable) {
        Criteria criteria = EntityObjectUtil.buildCriteria(new Criteria(), enterpriseModel, EntityObjectUtil.CriteriaType.Like, "name", "remark");
        if (enterpriseModel.getInfo() != null) {
            enterpriseModel.getInfo().entrySet().forEach((it) -> {
                if (StringUtils.hasText(it.getKey())) {
                    criteria.and("info." + it.getKey()).regex(String.valueOf(it.getValue()));
                }
            });
        }
        Query query = Query.query(criteria);
        return this.dbHelper.pages(query, pageable, Enterprise.class);
    }

    @Override
    public boolean update(EnterpriseModel enterpriseModel) {
        Query query = Query.query(Criteria.where("_id").is(enterpriseModel.getId()));
        Enterprise enterprise = this.mongoTemplate.findOne(query, Enterprise.class);
        if (enterprise == null) {
            return false;
        }

        //忽略不需要修改的字段
        Set<String> ignore = new HashSet<>() {{
            add("info");
        }};
        BeanUtil.getNullPropertyNames(enterpriseModel, ignore);
        BeanUtils.copyProperties(enterpriseModel, enterprise, ignore.toArray(new String[0]));


        //增量修改info
        if (enterpriseModel.getInfo() != null) {
            if (enterprise.getInfo() == null) {
                enterprise.setInfo(new HashMap<>());
            }
            for (Map.Entry<String, String> it : enterpriseModel.getInfo().entrySet()) {
                enterprise.getInfo().put(it.getKey(), it.getValue());
            }
        }

        try {
            this.mongoTemplate.save(enterprise);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }

        return StringUtils.hasText(enterprise.getId());
    }

    @Override
    public void reIndexInfo() {
        log.info("重建索引 : " + Enterprise.class.getSimpleName());
        reIndexHelper.reIndexFromMap(Enterprise.class, "info");
    }
}
