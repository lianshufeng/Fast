package com.fast.dev.component.remotelock.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * 观察者
 *
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2018年1月17日
 */
public class WatcherImpl implements Watcher {
    static Log log = LogFactory.getLog(WatcherImpl.class);

    private RemoteLockZooKeeper remoteLock;

    public WatcherImpl(RemoteLockZooKeeper remoteLock) {
        super();
        this.remoteLock = remoteLock;
    }

    @Override
    public void process(WatchedEvent event) {
        log.info(event);
        Event.KeeperState keeperState = event.getState();
        if (Event.KeeperState.Disconnected == keeperState || Event.KeeperState.Expired == keeperState) {
            this.remoteLock.noticeAllThreadError();
        }
    }

}
