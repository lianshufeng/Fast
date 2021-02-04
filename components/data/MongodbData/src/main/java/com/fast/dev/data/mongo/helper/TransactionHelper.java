package com.fast.dev.data.mongo.helper;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;

@Component
public class TransactionHelper {


    @Autowired
    private TransactionTemplate transactionTemplate;


    /**
     * 创建一个新事务
     *
     * @param action
     * @param <T>
     */
    @SneakyThrows
    public <T> T newTransaction(TransactionCallback<T> action) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Object[] ts = new Object[1];
        new Thread(() -> {
            try {
                ts[0] = transaction(action);
            } catch (TransactionException e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        }).start();
        countDownLatch.await();
        return ts[0] == null ? null : (T) ts[0];
    }


    /**
     * 创建一个没有事务的操作执行环境
     *
     * @param runnable
     */
    @SneakyThrows
    public void noTransaction(Runnable runnable) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        }).start();
        countDownLatch.await();
    }


    /**
     * 编程事务
     */
    public <T> T transaction(TransactionCallback<T> action) {
        return transactionTemplate.execute(action);
    }

    /**
     * 回滚事务
     */
    public void rollbackTransaction() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }


    /**
     * 提交事务
     * 注： 不可多次提交
     */
    public void commitTransaction() {
        transactionTemplate.getTransactionManager().commit(TransactionAspectSupport.currentTransactionStatus());
    }


}
