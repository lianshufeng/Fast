package com.fast.dev.robot.robotserver.core.dao.extend;

import com.fast.dev.robot.robotserver.core.domain.PhoneCodeRecord;

public interface PhoneCodeRecordDaoExtend {

    /**
     * 查询且减少尝试次数
     * @return
     */
    PhoneCodeRecord queryAndIncTryCount(String token);


}
