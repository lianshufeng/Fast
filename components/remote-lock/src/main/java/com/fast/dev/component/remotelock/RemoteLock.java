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
    public abstract SyncToken sync(String name) throws Exception;

    /**
     * 资源令牌,仅一根线程获得资源令牌
     *
     * @param name
     * @return 获得返回true, 否则为false
     * @throws Exception
     */
    public abstract boolean lock(String name) throws Exception;

    /**
     * 解锁资源
     *
     * @param name
     * @throws Exception
     */
    public abstract void unLock(String name) throws Exception;

}
