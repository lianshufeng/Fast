package com.fast.dev.component.remotelock;

import com.fast.dev.component.remotelock.conf.LockOption;

/**
 * 远程锁
 *
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2018年1月17日
 */
public abstract class RemoteLock {

    private LockOption option;

    /**
     * @return the option
     */
    public LockOption getOption() {
        return option;
    }

    public RemoteLock(LockOption option) {
        super();
        this.option = option;
    }

    /**
     * 资源锁, 所有线程依次执行
     *
     * @param name
     */
    public abstract SyncToken lock(String name) throws Exception;


}
