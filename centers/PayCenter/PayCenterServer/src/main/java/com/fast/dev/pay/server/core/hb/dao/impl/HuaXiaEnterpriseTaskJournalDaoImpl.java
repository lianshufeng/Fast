package com.fast.dev.pay.server.core.hb.dao.impl;

import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaEnterpriseTaskJournalDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTaskJournal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.HashSet;
import java.util.Set;

public class HuaXiaEnterpriseTaskJournalDaoImpl implements HuaXiaEnterpriseTaskJournalDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;


    @Override
    public HuaXiaEnterpriseTaskJournal buildTaskJournal() {
        HuaXiaEnterpriseTaskJournal huaXiaEnterpriseTaskJournal = new HuaXiaEnterpriseTaskJournal();
        this.dbHelper.saveTime(huaXiaEnterpriseTaskJournal);
        this.mongoTemplate.insert(huaXiaEnterpriseTaskJournal);
        return huaXiaEnterpriseTaskJournal;
    }


    @Override
    public void updateTaskJournal(HuaXiaEnterpriseTaskJournal journal) {
        //仅更新需要修改的数据
        Set<String> ignore = new HashSet<>();
        BeanUtil.getNullPropertyNames(journal, ignore);

        Update update = new Update();
        EntityObjectUtil.entity2Update(journal, update, ignore);

        this.dbHelper.updateTime(update);
        this.mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(journal.getId())), update, HuaXiaEnterpriseTaskJournal.class);

    }
}
