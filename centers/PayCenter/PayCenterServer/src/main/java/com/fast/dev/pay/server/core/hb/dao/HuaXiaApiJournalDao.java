package com.fast.dev.pay.server.core.hb.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaApiJournalDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaApiJournal;

public interface HuaXiaApiJournalDao extends MongoDao<HuaXiaApiJournal>, HuaXiaApiJournalDaoExtend {


}
