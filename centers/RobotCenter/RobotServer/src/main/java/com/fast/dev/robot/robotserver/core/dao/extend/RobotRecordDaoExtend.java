package com.fast.dev.robot.robotserver.core.dao.extend;

import com.fast.dev.robot.robotserver.core.domain.RobotRecord;
import com.fast.dev.robot.service.type.RobotType;

import java.util.Map;

public interface RobotRecordDaoExtend {


    /**
     * 获取机器码记录
     *
     * @return
     */
    RobotRecord getRobotRecord(String token);


    /**
     * 清楚超时的记录
     *
     * @return
     */
    long cleanTimeOutRecord(Map<RobotType, Long> records);


}
