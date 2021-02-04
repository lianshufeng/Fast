package com.fast.dev.data.token.service.impl;

import com.fast.dev.data.mongo.helper.TransactionHelper;
import com.fast.dev.data.token.dao.ResourceTokenDao;
import com.fast.dev.data.token.domain.ResourceToken;
import com.fast.dev.data.token.service.ResourceTokenService;
import com.fast.dev.data.mongo.helper.DBHelper;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


/**
 * 资源令牌业务
 */
@Slf4j
public class ResourceTokenServiceImpl extends ResourceTokenService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ResourceTokenDao resourceTokenDao;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private TransactionHelper transactionHelper;

    //资源超时时间
    private static final long ResourceTokenTimeOut = 1000 * 60;


    //监视mongo删除数据的线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    //存储内存里的资源锁定令牌
    private Map<String, Vector<LockTokenImpl>> lockTokenMap = new ConcurrentHashMap<>();

    @PreDestroy
    public void shutdown() {
        executorService.shutdownNow();
    }

    @Autowired
    private void init() {
        //初始化数据库监听
        initListen();

        //设置调度器
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                heartbeat();
            }
        }, 0, 10000);

        // 定时触发器
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkUnlockToken();
            }
        }, 0, 1000);

    }

    private Timer timer = new Timer();


    /**
     * 发送心跳
     */
    private synchronized void heartbeat() {
        for (Vector<LockTokenImpl> lockTokens : lockTokenMap.values()) {
            synchronized (lockTokens) {
                List<ResourceToken> resourceTokenModels = lockTokens.stream().map((it) -> {
                    ResourceToken resourceToken = new ResourceToken();
                    resourceToken.setCounter(it.getCounter());
                    resourceToken.setResourceName(it.getResourceName());
                    return resourceToken;
                }).collect(Collectors.toList());
                this.resourceTokenDao.updateTTL(ttlTime(), resourceTokenModels.toArray(new ResourceToken[0]));
            }
        }

    }


    private void initListen() {
        //仅监视删除的数据
        this.executorService.execute(() -> {
            this.mongoTemplate.getCollection(this.mongoTemplate.getCollectionName(ResourceToken.class)).watch(new ArrayList<>() {{
                add(new Document("$match", new Document("operationType", "delete")));
            }}).forEach((it) -> {
                checkUnlockToken();
            });
        });
    }


    @Override
    public Token token(final String resourceName) {
        Assert.hasText(resourceName, "锁定的资源不能为空");

        //资源计数器
        long counter = addRecord(resourceName);

        //生成资源令牌
        LockTokenImpl token = makeToken(resourceName, counter);

        //当前线程阻塞或执行
        await(token);

        return token;
    }

    @Override
    public void token(String resourceName, Runnable runnable) {
        @Cleanup Token token = token(resourceName);
        runnable.run();
    }

    /**
     * 检查是否可以解锁令牌
     */
    @SneakyThrows
    private synchronized void checkUnlockToken() {
        if (this.lockTokenMap.size() == 0) {
            return;
        }
        this.lockTokenMap.entrySet().forEach((it) -> {
            executorService.execute(() -> {
                try {
                    //检查是否能激活token
                    checkActiveToken(it.getKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    /**
     * 检查是否可以激活token
     */
    private void checkActiveToken(String resourceName) {
        //取出DB的Counter最小的数据
        Optional.ofNullable(this.resourceTokenDao.findTop1ByResourceNameAndTypeOrderByCounterAsc(resourceName, ResourceToken.ResourceType.User)).ifPresent((resourceToken) -> {
            Optional.ofNullable(this.lockTokenMap.get(resourceName)).ifPresent((items) -> {
                synchronized (items) {
                    //取出内存中最小的数
                    items.stream().min((t1, t2) -> {
                        return Long.valueOf((t1.getCounter() - t2.getCounter())).intValue();
                    }).ifPresent((it) -> {
                        //如果最小的任务没有执行，则激活任务
                        if (resourceToken.getCounter() == it.getCounter() && !it.isRun()) {
                            activeToken(it);
                        }
                    });
                }
            });
        });


    }


    /**
     * 激活锁定流程
     *
     * @param lockToken
     */
    private void activeToken(LockTokenImpl lockToken) {
        log.debug("激活任务 : {} -> {}", lockToken.getResourceName(), lockToken.getCounter());
        lockToken.run();
    }


    /**
     * 删除本地的令牌
     *
     * @param token
     */
    protected void removeLocalToken(LockTokenImpl token) {
        Optional.ofNullable(this.lockTokenMap.get(token.getResourceName())).ifPresent((it) -> {
            it.remove(token);
        });
    }


    /**
     * 删除token
     *
     * @param token
     */
    protected void removeRemoteLockToken(LockTokenImpl token) {
        this.transactionHelper.noTransaction(() -> {
            this.resourceTokenDao.removeByResourceNameAndCounterAndType(token.getResourceName(), token.getCounter(), ResourceToken.ResourceType.User);
        });
    }


    //校验是否需要阻塞
    private void await(LockTokenImpl token) {
        token.await();
    }

    /**
     * 生产资源锁令牌
     *
     * @param counter
     * @return
     */
    private LockTokenImpl makeToken(String resourceName, long counter) {
        LockTokenImpl token = new LockTokenImpl();
        token.setResourceTokenService(this);

        token.setCounter(counter);
        token.setResourceName(resourceName);

        //追加到内存对象里
        appendToItems(token);
        return token;
    }


    /**
     * 将token增加到内存里
     */
    private synchronized void appendToItems(LockTokenImpl lockToken) {
        Vector<LockTokenImpl> items = lockTokenMap.get(lockToken.getResourceName());
        if (items == null) {
            items = new Vector<>();
            this.lockTokenMap.put(lockToken.getResourceName(), items);
        }
        items.add(lockToken);
    }


    /**
     * 添加记录
     *
     * @param resourceName
     * @return
     */
    private long addRecord(String resourceName) {
        long[] ts = new long[1];
        this.transactionHelper.noTransaction(() -> {
            long counter = this.resourceTokenDao.counter(resourceName);
            this.resourceTokenDao.record(resourceName, counter, ttlTime());
            ts[0] = counter;
        });
        return ts[0];
    }

    private long ttlTime() {
        return this.dbHelper.getTime() + ResourceTokenTimeOut;
    }


    public static class LockTokenImpl extends Token {

        @Setter
        private ResourceTokenServiceImpl resourceTokenService;


        CountDownLatch countDownLatch = new CountDownLatch(1);

        @Getter
        @Setter
        private long counter;

        @Getter
        @Setter
        private String resourceName;


        /**
         * 开始
         */
        @SneakyThrows
        protected void await() {
            countDownLatch.await();
        }

        /**
         * 执行
         */
        protected void run() {
            countDownLatch.countDown();
        }

        /**
         * 任务是否在执行
         *
         * @return
         */
        protected boolean isRun() {
            return countDownLatch.getCount() == 0;
        }

        /**
         * 释放资源
         */
        @Override
        public synchronized void close() {
            //顺序很重要
            this.resourceTokenService.removeLocalToken(this);
            //从队列中删除当前令牌
            this.resourceTokenService.removeRemoteLockToken(this);
        }
    }

}

