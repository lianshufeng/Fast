package com.fast.dev.ucenter.core.scheduled;

import com.fast.dev.ucenter.core.domain.RequestUrlLog;
import com.fast.dev.ucenter.core.domain.UserTokenLog;
import com.fast.dev.ucenter.core.service.LocalUserService;
import com.fast.dev.ucenter.core.service.RequestUrlLogService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 清除过期数据
 */
@EnableScheduling
@Component
@Log
public class CleanTimeOutRecordsScheduled {

    @Autowired
    private LocalUserService localUserService;

    @Autowired
    private RequestUrlLogService requestUrlLogService;


    private final static long logSaveMaxTime = 1000 * 60 * 60 * 24 * 7;

    @Scheduled(cron = "0 0 * * * ?")
    public void cleanOutUserTokenLog() {
        long size = this.localUserService.cleanTimeOutRecord(UserTokenLog.class, logSaveMaxTime);
        if (size > 0) {
            log.info(UserTokenLog.class + " remove  : [" + size + "]");
        }
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void clean() {
        long size = this.localUserService.cleanTimeOutRecord(RequestUrlLog.class, logSaveMaxTime);
        if (size > 0) {
            log.info(RequestUrlLog.class + " remove  : [" + size + "]");
        }
    }


}
