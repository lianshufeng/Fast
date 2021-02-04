package com.fast.dev.component.remotelock.impl;

import lombok.extern.java.Log;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

/**
 * 获取锁定令牌
 */

@Log
public class GetLockToken extends GeneralLockToken {

    private String nodePath;


    public GetLockToken(RemoteLockZooKeeper remoteLock, String name) throws Exception {
        super(remoteLock, name);
    }

    @Override
    public void lock() {
        //默认为获取资源失败
        this.canRun = false;
        // 令牌
        try {
            this.nodePath = this.remoteLock.zk.create(getWriteNodePath(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL);
            this.canRun = true;
        } catch (Exception e) {
            this.canRun = false;
        }
    }


    @Override
    public void release() throws Exception {
        if (this.nodePath != null) {
            this.remoteLock.zk.delete(getWriteNodePath(), -1);
        }
    }


    //需要覆写本方法，但不需要做任何操作，因为需要在lock的时候创建
    @Override
    public void createUserNode() throws KeeperException, InterruptedException {

    }

    /**
     * 获取要写入的节点
     *
     * @return
     */
    private String getWriteNodePath() {
        return userNodePath();
    }


}
