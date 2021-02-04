package com.fast.dev.pay.server.core.hb.dao.impl;

import com.fast.dev.core.util.text.TextUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.server.core.dao.IncTableDao;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaApiJournalDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaApiJournal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HuaXiaApiJournalDaoImpl implements HuaXiaApiJournalDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IncTableDao incTableDao;

    @Autowired
    private DBHelper dbHelper;


    //流水自增号长度
    private final static int MaxTradeNoIncLength = 5;

    private final static String HbApiJournal = "HbApiJournal";

    @Override
    public HuaXiaApiJournal create(String methodName) {
        String HbApiJournalIncName = String.format("HbApiJournal_%s", makePcsDate());
        long counter = incTableDao.inc(HbApiJournalIncName, 10000l);


        HuaXiaApiJournal journal = new HuaXiaApiJournal();
        journal.setTradeNo(makeTradeNo(counter));
        journal.setPcsDate(makePcsDate());
        journal.setMethod(methodName);

        this.dbHelper.saveTime(journal);
        this.mongoTemplate.insert(journal);

        return journal;
    }

    @Override
    public boolean updateRequest(String id, String request) {
        Update update = new Update();
        update.set("request", request);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)), update, HuaXiaApiJournal.class).getModifiedCount() > 0;
    }

    @Override
    public boolean updateResponse(String id, String response) {
        Update update = new Update();
        update.set("response", response);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)), update, HuaXiaApiJournal.class).getModifiedCount() > 0;
    }

    @Override
    public boolean updateTTL(String id, long ttlTime) {
        Update update = new Update();
        update.set("TTL", new Date(ttlTime));
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)), update, HuaXiaApiJournal.class).getModifiedCount() > 0;
    }


    /**
     * 创建交易流水号
     *
     * @param counter
     * @return
     */
    private String makeTradeNo(long counter) {
        return makeTradeNoTimeText() + TextUtil.fill('0', MaxTradeNoIncLength, counter);
    }

    /**
     * 创建交易时间
     *
     * @return
     */
    private String makePcsDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(new Date(this.dbHelper.getTime()));
    }


    /**
     * 制作交易流水号时间文本
     *
     * @return
     */
    private String makeTradeNoTimeText() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return simpleDateFormat.format(new Date(this.dbHelper.getTime()));
    }


}
