package com.fast.dev.robot.robotserver.core.service;

import com.fast.dev.robot.robotserver.core.dao.RobotRecordDao;
import com.fast.dev.robot.robotserver.core.domain.RobotRecord;
import com.fast.dev.robot.robotserver.core.model.RobotValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RobotRecordService {

    @Autowired
    private RobotRecordDao robotRecordDao;

    @Transactional
    public RobotValidate getRobotRecord(String token) {
        RobotRecord robotRecord = this.robotRecordDao.getRobotRecord(token);
        if (robotRecord == null) {
            return null;
        }
        return robotRecord.getRobotValidate();
    }







}
