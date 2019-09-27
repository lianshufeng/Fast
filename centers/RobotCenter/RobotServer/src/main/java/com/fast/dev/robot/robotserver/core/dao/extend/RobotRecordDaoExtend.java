package com.fast.dev.robot.robotserver.core.dao.extend;

import com.fast.dev.robot.robotserver.core.domain.RobotRecord;

public interface RobotRecordDaoExtend {


    /**
     * 获取机器码记录
     * @return
     */
    RobotRecord getRobotRecord(String token);
}
