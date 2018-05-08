package com.fast.dev.component.remotelock.impl;

import com.fast.dev.component.remotelock.SyncToken;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class GeneralLockToken implements SyncToken {

    static Log log = LogFactory.getLog(GeneralLockToken.class);
    // 默认的前缀
    final static String defaultNodePreName = "lock_";
    // 远程锁
    protected RemoteLockZooKeeper remoteLock;
    // 锁定的名称
    protected String name;
    // 是否已创建用户节点
    private static Boolean isCreateUserNode = false;
    // 阻塞进程
    protected final CountDownLatch downLatch = new CountDownLatch(1);
    // 可否执行
    protected boolean canRun = true;

    public GeneralLockToken(RemoteLockZooKeeper remoteLock, String name) throws KeeperException, InterruptedException {
        super();
        this.remoteLock = remoteLock;
        this.name = name;
        // 创建用户节点
        createUserNode();
        // 锁定
        lock();
    }

    /**
     * 创建用户节点
     *
     * @throws InterruptedException
     * @throws KeeperException
     */
    public void createUserNode() throws KeeperException, InterruptedException {
        synchronized (isCreateUserNode) {
            if (isCreateUserNode) {
                return;
            }
            this.remoteLock.createNode(userNodePath());
            isCreateUserNode = true;
        }
    }

    /**
     * @throws InterruptedException
     * @throws KeeperException
     * @throws Exception
     */
    public abstract void lock() throws KeeperException, InterruptedException;

    /**
     * 用户节点路径
     *
     * @return
     */
    protected String userNodePath() {
        return this.remoteLock.serviceNodePath() + "/" + this.name;
    }

    /**
     * @return
     */
    protected String nodeUserPrePath() {
        return userNodePath() + "/" + defaultNodePreName;
    }

    /**
     * 获取节点名字
     *
     * @param tokenName
     * @return
     */
    protected String nodeUserPath(String tokenName) {
        return userNodePath() + "/" + tokenName;
    }

    /**
     * 激活流程
     */
    protected void threadActive() {
        downLatch.countDown();
    }

    /**
     * 阻塞流程
     *
     * @throws InterruptedException
     */
    protected void threadWait() throws InterruptedException {
        downLatch.await(this.remoteLock.option.getMaxThreadWaitTime(), TimeUnit.MILLISECONDS);
    }

}
