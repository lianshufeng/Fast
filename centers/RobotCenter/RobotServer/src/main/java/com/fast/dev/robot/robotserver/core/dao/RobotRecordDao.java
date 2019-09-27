package com.fast.dev.robot.robotserver.core.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.robot.robotserver.core.dao.extend.RobotRecordDaoExtend;
import com.fast.dev.robot.robotserver.core.domain.RobotRecord;

/**
 * 记录dao
 */
public interface RobotRecordDao extends MongoDao<RobotRecord>, RobotRecordDaoExtend {


}
