package com.fast.dev.component.remotelock.impl;

import com.fast.dev.component.remotelock.RemoteLock;
import com.fast.dev.component.remotelock.SyncToken;
import com.fast.dev.component.remotelock.conf.LockOption;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 远程资源锁的实现
 *
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2018年1月17日
 */

@Log
@Component
public class RemoteLockZooKeeper extends RemoteLock {

    @Getter
    @Autowired
    private LockOption option;


    @Autowired
    private ApplicationContext applicationContext;

    private Map<Thread, SyncToken> threadCounterMap = new ConcurrentHashMap<>();

    //连接
    private CountDownLatch connectionZKDownLatch = new CountDownLatch(1);


    public final static String DefaultTempLockRoot = "_remote_lock_";
    // 默认配置

    // 当前客户端
    public ZooKeeper zk;
    // 资源锁
    private ResourcesLockToken resourcesLockToken = new ResourcesLockToken(this);


    /**
     * 构造方法
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    @PostConstruct
    private void initRemoteLockZooKeeper() throws IOException, KeeperException, InterruptedException {
        // 创建客户端
        createClient();
        // 创建根节点
        createNode(rootNodePath());
        // 创建业务节点
        createNode(serviceNodePath());
    }

    @Override
    public SyncToken queue(String name) throws Exception {
        return returnSyncToken(new QueueLockToken(this, name));
    }


    @Override
    public SyncToken get(String name) throws Exception {
        return returnSyncToken(new GetLockToken(this, name));
    }

    /**
     * 判断返回是否为空的对象
     *
     * @param lockToken
     * @return
     */
    private static SyncToken returnSyncToken(GeneralLockToken lockToken) {
        if (!lockToken.canRun) {
            return null;
        }
        return lockToken;
    }


    @PreDestroy
    private void close() throws Exception {
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
        zk = new ZooKeeper(this.option.getHostConnectString(), this.option.getSessionTimeout(), new Watcher() {

            @Override
            @SneakyThrows
            public void process(WatchedEvent event) {
                Event.KeeperState keeperState = event.getState();
                if (Event.KeeperState.SyncConnected == keeperState) {
                    connectionZKDownLatch.countDown();
                } else if (event.getState() == Event.KeeperState.Expired) {
                    //自动重连
                    log.info("会话超时，自动重连zk");
                    createClient();
                } else {
                    log.info("zk State : " + event.getState());
                    throw new RuntimeException("remote lock create error");
                }
            }
        });
        //阻塞并等待连接
        try {
            connectionZKDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
            log.info("create node :" + nodePath);
        }
    }

    /**
     * 取业务节点名
     *
     * @return
     */
    public String serviceNodePath() {
        return rootNodePath() + "/" + this.option.getNameSpace();
    }

    /**
     * 获取根节点名
     *
     * @return
     */
    public String rootNodePath() {
        return "/" + RemoteLockZooKeeper.DefaultTempLockRoot;
    }


}
