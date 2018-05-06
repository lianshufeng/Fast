package com.fast.dev.component.remotelock.impl;

import com.fast.dev.component.remotelock.RemoteLock;
import com.fast.dev.component.remotelock.SyncToken;
import com.fast.dev.component.remotelock.conf.LockOption;
import com.fast.dev.component.remotelock.exception.ServerErrorException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程资源锁的实现
 *
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2018年1月17日
 */
public class RemoteLockZooKeeper extends RemoteLock {
    static Log log = LogFactory.getLog(RemoteLockZooKeeper.class);

    private Map<Thread, SyncLockToken> threadCounterMap = new ConcurrentHashMap<Thread, SyncLockToken>();

    public final static String DefaultTempLockRoot = "_remote_lock_";
    // 默认配置
    public LockOption option;
    // 当前客户端
    public ZooKeeper zk;
    // 资源锁
    private ResourcesLockToken resourcesLockToken = new ResourcesLockToken(this);

    /**
     * 构造方法
     *
     * @param option
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    public RemoteLockZooKeeper(LockOption option) throws IOException, KeeperException, InterruptedException {
        super(option);
        this.option = option;
        // 创建客户端
        createClient();
        // 创建根节点
        createNode(rootNodePath());
        // 创建业务节点
        createNode(serviceNodePath());
    }

    @Override
    public SyncToken sync(String name) throws KeeperException, InterruptedException {
        SyncLockToken actionToken = new SyncLockToken(this, name);
        // 不可执行
        if (!actionToken.canRun) {
            throw new RuntimeException(new ServerErrorException());
        }
        return actionToken;
    }

    @Override
    public boolean lock(String name) throws Exception {
        return this.resourcesLockToken.lock(name);
    }


    @Override
    public void unLock(String name) throws Exception {
        this.resourcesLockToken.unLock(name);
    }


    public void close() throws Exception {
        if (this.zk != null) {
            this.zk.close();
        }
    }

    /**
     * 创建客户端
     *
     * @throws IOException
     */
    private void createClient() throws IOException {
        zk = new ZooKeeper(this.option.getHostConnectString(), this.option.getSessionTimeout(), new WatcherImpl(this));
    }

    /**
     * 创建业务节点
     *
     * @throws InterruptedException
     * @throws KeeperException
     */
    public void createNode(String nodePath) throws KeeperException, InterruptedException {
        // 根节点
        Stat stat = zk.exists(nodePath, false);
        if (stat == null) {
            this.zk.create(nodePath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            log.debug("create node :" + nodePath);
        }
    }

    /**
     * 取业务节点名
     *
     * @return
     */
    public String serviceNodePath() {
        return rootNodePath() + "/" + this.option.getServiceName();
    }

    /**
     * 获取根节点名
     *
     * @return
     */
    public String rootNodePath() {
        return "/" + RemoteLockZooKeeper.DefaultTempLockRoot;
    }

    /**
     * 设置线程阻塞
     *
     * @param countDownLatch
     */
    protected void putThreadCounter(SyncLockToken safeLockToken) {
        this.threadCounterMap.put(Thread.currentThread(), safeLockToken);
    }

    /**
     * 移出计数器
     */
    protected void finishThreadCounter() {
        this.threadCounterMap.remove(Thread.currentThread());
    }

    /**
     * 通知所有阻塞的线程不可执行
     */
    protected void noticeAllThreadError() {
        for (SyncLockToken lockToken : this.threadCounterMap.values()) {
            lockToken.canRun = false;
            lockToken.downLatch.countDown();
        }
    }

}
