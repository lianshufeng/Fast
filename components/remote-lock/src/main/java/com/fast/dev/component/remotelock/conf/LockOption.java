package com.fast.dev.component.remotelock.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 远程锁配置对象
 *
 * @作者 练书锋
 * @联系 251708339@qq.com
 * @时间 2018年1月17日
 */
@Component
@ConfigurationProperties(prefix = "remotelock")
public class LockOption {

    // 会话超时时间
    private int sessionTimeout = 5000;

    // 最大线程阻塞的时间
    private long maxThreadWaitTime = 1000 * 60 * 60;

    //主机连接字符串
    private String hostConnectString;

    // 业务名
    private String serviceName = "_default_";

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the sessionTimeout
     */
    public int getSessionTimeout() {
        return sessionTimeout;
    }

    /**
     * @param sessionTimeout the sessionTimeout to set
     */
    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }


    /**
     * @return the maxThreadWaitTime
     */
    public long getMaxThreadWaitTime() {
        return maxThreadWaitTime;
    }

    /**
     * @param maxThreadWaitTime the maxThreadWaitTime to set
     */
    public void setMaxThreadWaitTime(long maxThreadWaitTime) {
        this.maxThreadWaitTime = maxThreadWaitTime;
    }


    public String getHostConnectString() {
        return hostConnectString;
    }

    public void setHostConnectString(String hostConnectString) {
        this.hostConnectString = hostConnectString;
    }

    public LockOption(String hostConnectString) {
        this.hostConnectString = hostConnectString;
    }

    public LockOption() {
        // TODO Auto-generated constructor stub
    }

}
