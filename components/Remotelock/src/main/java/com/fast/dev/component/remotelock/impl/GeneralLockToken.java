package com.fast.dev.component.remotelock.impl;

import com.fast.dev.component.remotelock.SyncToken;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
public abstract class GeneralLockToken implements SyncToken {

    static Log log = LogFactory.getLog(GeneralLockToken.class);
    // 默认的前缀
    final static String defaultNodePreName = "_lock_";
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


    /**
     * 支持无参数构造
     *
     * @param remoteLock
     * @param name
     * @throws KeeperException
     * @throws InterruptedException
     */
    protected void init(RemoteLockZooKeeper remoteLock, String name) throws Exception {
        this.remoteLock = remoteLock;
        this.name = name;
        // 创建用户节点
        createUserNode();
        // 锁定
        lock();
    }

    public GeneralLockToken(RemoteLockZooKeeper remoteLock, String name) throws Exception {
        super();
        init(remoteLock, name);
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
    public abstract void lock() throws Exception;

    /**
     * 用户节点路径
     *
     * @return
     */
    protected String userNodePath() {
        return this.remoteLock.serviceNodePath();
    }

    /**
     * @return
     */
    protected String nodeUserPrePath() {
        return userNodePath() + "/" + this.name + defaultNodePreName;
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
//        downLatch.await(this.remoteLock.getOption().getMaxThreadWaitTime(), TimeUnit.MILLISECONDS);
        downLatch.await();
    }

}
