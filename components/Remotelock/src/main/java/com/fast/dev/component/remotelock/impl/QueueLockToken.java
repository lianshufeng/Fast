package com.fast.dev.component.remotelock.impl;

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;

/**
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2018年1月18日
 */
public class QueueLockToken extends GeneralLockToken {

    // 节点路径
    private String nodePath;
    // 令牌名称
    private String token;

    public QueueLockToken(RemoteLockZooKeeper remoteLock, String name) throws Exception {
        super(remoteLock, name);
    }

    @Override
    public void lock() throws  Exception {
        // 创建节点的名称
        String nodeName = nodeUserPrePath();
        // 令牌
        this.nodePath = this.remoteLock.zk.create(nodeName, null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
        // 取出令牌名
        this.token = this.nodePath.substring(this.nodePath.lastIndexOf("/") + 1);
        // 获取当前令牌的前一个令牌
        String beforeToken = getBeforeToken();
        // 可否锁定
        if (canLock(beforeToken)) {
            super.threadWait();
        }

    }

    /**
     * 锁定线程
     *
     * @throws InterruptedException
     * @throws KeeperException
     */
    private boolean canLock(String beforeToken) throws KeeperException, InterruptedException {
        // 如果没有前一个令牌，则为可执行线程，否则阻塞
        if (beforeToken == null) {
            return false;
        }
        // 前一个令牌的路径
        final String beforePath = nodeUserPath(beforeToken);
        try {
            Stat stat = this.remoteLock.zk.exists(beforePath, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    try {
                        if (event.getType() == EventType.NodeDeleted && canUnLock()) {
                            QueueLockToken.this.threadActive();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if (stat != null) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 可否解锁
     *
     * @return
     * @throws InterruptedException
     * @throws KeeperException
     */
    private boolean canUnLock() throws KeeperException, InterruptedException {
        // 获取当前令牌的前一个令牌
        String beforeToken = getBeforeToken();
        // 如果没有前一个令牌，则为可执行线程，否则阻塞
        if (beforeToken == null) {
            return true;
        }
        return !canLock(beforeToken);
    }

    /**
     * 获取当前令牌的前一个令牌
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    private String getBeforeToken() throws KeeperException, InterruptedException {
        // 获取所有的节点
        List<String> nodes = this.remoteLock.zk.getChildren(this.userNodePath(), false);
        Collections.sort(nodes);
        // 获得执行令牌
        int at = nodes.indexOf(this.token);
        return at == 0 ? null : nodes.get(at - 1);
    }

    /**
     * 释放
     *
     * @throws InterruptedException
     * @throws KeeperException
     */
    @Override
    public void release() throws InterruptedException, KeeperException {
        if (this.nodePath != null) {
            this.remoteLock.zk.delete(this.nodePath, -1);
        }
    }

}
