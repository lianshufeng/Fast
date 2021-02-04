package com.fast.dev.robot.robotserver.core.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.robot.robotserver.core.dao.extend.PhoneCodeRecordDaoExtend;
import com.fast.dev.robot.robotserver.core.domain.PhoneCodeRecord;

/**
 * 手机验证码
 */
public interface PhoneCodeRecordDao extends MongoDao<PhoneCodeRecord>, PhoneCodeRecordDaoExtend {

}
