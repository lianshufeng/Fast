package com.fast.dev.gateway.core.service;

import com.fast.dev.core.util.os.SystemUtil;
import com.fast.dev.gateway.core.dao.AccessDao;
import com.fast.dev.gateway.core.dao.BlacklistDao;
import com.fast.dev.gateway.core.domain.AccessRecord;
import com.fast.dev.gateway.core.model.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AccessService {

    @Autowired
    private AccessDao accessDao;

    @Autowired
    private BlacklistDao blacklistDao;


    private ExecutorService executorService;


    @Autowired
    private void init(ApplicationContext applicationContext) {
        executorService = Executors.newFixedThreadPool(SystemUtil.getCpuCoreCount() * 2);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdownNow();
        }));
    }

    /**
     * 记录数据
     *
     * @param
     */
    public void record(final Policy policy, final AccessRecord accessRecord) {
        executorService.execute(() -> {
            String key = null;
            try {
                key = this.accessDao.record(policy, accessRecord);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //处理是否要进入黑名单
            try {
                blackList(policy, key, accessRecord.getIp(), accessRecord.getRoleName());
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }


    /**
     * 是否需要加入到黑名单中
     */
    private void blackList(Policy policy, String key, String ip, String roleName) {
        if (this.accessDao.count(key) > policy.getAccessCount()) {
            this.blacklistDao.append(policy.getBlockTime(), ip, roleName);
        }
    }


}
