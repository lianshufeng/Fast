package com.fast.dev.component.remotelock.impl;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.springframework.util.StringUtils;

/**
 * 资源锁令牌
 *
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2018年1月20日
 */
public class ResourcesLockToken {

    // 远程客户端实现
    private RemoteLockZooKeeper remoteLock;

    public ResourcesLockToken(RemoteLockZooKeeper remoteLock) {
        super();
        this.remoteLock = remoteLock;
    }

    /**
     * 锁定资源
     *
     * @return
     */
    public boolean lock(String name) {
        return resources(name);
    }

    /**
     * 解锁资源
     *
     * @param name
     */
    public void unLock(String name) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        try {
            this.remoteLock.zk.delete(nodePath(name), -1);
        } catch (Exception e) {
        }
    }

    /**
     * 锁定资源
     *
     * @return
     */
    private boolean resources(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        try {
            String newNodePath = this.remoteLock.zk.create(nodePath(name), null, Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL);
            return !StringUtils.isEmpty(newNodePath);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 取节点名称
     *
     * @return
     */
    private String nodePath(String name) {
        return this.remoteLock.serviceNodePath() + "/" + name;
    }

}
