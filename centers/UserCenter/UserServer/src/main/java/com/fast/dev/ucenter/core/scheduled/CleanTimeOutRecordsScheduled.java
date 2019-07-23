package com.fast.dev.ucenter.core.scheduled;

import com.fast.dev.ucenter.core.domain.UserTokenLog;
import com.fast.dev.ucenter.core.service.LocalUserService;
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


    //    @Scheduled(cron = "* * 0/1 * * ?")
    @Scheduled(cron = "0 0 * * * ?")
    public void removeTimeOutUserTokenLog() {
        long size = this.localUserService.cleanTimeOutRecord(UserTokenLog.class, 1000 * 60 * 60 * 24 * 7);
        if (size > 0) {
            log.info(UserTokenLog.class + " remove  : [" + size + "]");
        }
    }

//    @Scheduled(cron = "* 0/1 * * * ?")
//    public void removeTimeOutServiceTokenLog() {
//
//    }

}
