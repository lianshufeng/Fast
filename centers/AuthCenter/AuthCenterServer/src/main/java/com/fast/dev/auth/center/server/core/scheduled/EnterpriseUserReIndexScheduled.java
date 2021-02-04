package com.fast.dev.auth.center.server.core.scheduled;

import com.fast.dev.auth.center.server.core.dao.EnterpriseDao;
import com.fast.dev.auth.center.server.core.dao.UserDao;
import com.fast.dev.data.token.service.ResourceTokenService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 企业用户重置索引
 */
@EnableScheduling
@Component
@Log
public class EnterpriseUserReIndexScheduled {


    /**
     * 任务名
     */
    private final static String taskName = "ReIndexTask";

    /**
     * 任务超时时间
     */
    private final static long taskTimeOut = 1000 * 60 * 60l;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EnterpriseDao enterpriseDao;


    @Autowired
    private ResourceTokenService resourceTokenService;


    @Scheduled(cron = "0 0/60 * * * ?")
    public void reUserInfoIndex() {
        resourceTokenService.token("reindex_user_info", () -> {
            this.userDao.reIndexInfo();
        });
    }


    @Scheduled(cron = "0 0/60 * * * ?")
    public void reEnterpriseInfoIndex() {
        resourceTokenService.token("reindex_enterprise_info", () -> {
            this.enterpriseDao.reIndexInfo();
        });
    }


}
