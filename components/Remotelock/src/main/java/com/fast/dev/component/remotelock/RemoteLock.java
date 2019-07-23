package com.fast.dev.component.remotelock;

/**
 * 远程锁
 *
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2018年1月17日
 */
public abstract class RemoteLock {


    /**
     * 添加到队列中，并执行
     *
     * @param name
     */
    public abstract SyncToken queue(String name) throws Exception;


    /**
     * 获取令牌，有且仅一个会成功，成功必须释放否则其他无法再获取，失败返回null
     *
     * @param name
     * @return
     * @throws Exception
     */
    public abstract SyncToken get(String name) throws Exception;

}
