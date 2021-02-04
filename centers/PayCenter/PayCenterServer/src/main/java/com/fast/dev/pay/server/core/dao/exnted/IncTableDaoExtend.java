package com.fast.dev.pay.server.core.dao.exnted;

public interface IncTableDaoExtend {


    /**
     * 计次
     *
     * @param serviceName
     * @return
     */
    long inc(String serviceName);


    /**
     * 计次
     *
     * @param serviceName
     * @param timeOut     ,超时时间
     * @return
     */
    long inc(String serviceName, Long timeOut);


    /**
     * 重置
     *
     * @param serviceName
     * @return
     */
    boolean reset(String serviceName);


}
