package com.fast.dev.component.remotelock;

/**
 * 行动令牌
 *
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2018年1月17日
 */
public interface SyncToken {

    /**
     * 解锁
     */
    void release() throws Exception;

}
