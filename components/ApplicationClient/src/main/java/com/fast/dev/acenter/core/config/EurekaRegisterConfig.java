package com.fast.dev.acenter.core.config;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.InstanceInfoFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.File;

@Log
@Configuration
public class EurekaRegisterConfig {

    private final static File docker_host_file = new File("/etc/docker_host_ip");
//    private final static File docker_host_file = new File("c:/docker_host_ip");


    @Bean
    @ConditionalOnMissingBean(value = ApplicationInfoManager.class, search = SearchStrategy.CURRENT)
    public ApplicationInfoManager eurekaApplicationInfoManager(EurekaInstanceConfig config) {
        //更新配置
        updateDockerHost(config);
        InstanceInfo instanceInfo = new InstanceInfoFactory().create(config);
        return new ApplicationInfoManager(config, instanceInfo);
    }


    //更新docker主机
    @SneakyThrows
    private void updateDockerHost(EurekaInstanceConfig config) {
        if (!docker_host_file.exists()) {
            return;
        }
        String docker_host_ip = FileUtils.readFileToString(docker_host_file, "UTF-8");
        docker_host_ip = docker_host_ip.replaceAll("\n", "");
        docker_host_ip = docker_host_ip.replaceAll("\r", "");
        if (!StringUtils.hasText(docker_host_ip)) {
            return;
        }

        //注册配置
        if (config instanceof EurekaInstanceConfigBean) {
            EurekaInstanceConfigBean eurekaInstanceConfigBean = (EurekaInstanceConfigBean) config;
            //获取原注册地址
            String ipAddress = eurekaInstanceConfigBean.getIpAddress();
            String hostName = eurekaInstanceConfigBean.getHostName(false);

            //设置新的地址
            updateHostInfo(eurekaInstanceConfigBean, ipAddress, docker_host_ip);
            updateHostInfo(eurekaInstanceConfigBean, hostName, docker_host_ip);

            log.info("update ip : " + ipAddress + " -> " + docker_host_ip);
        }

    }


    /**
     * 更新主机信息
     *
     * @param eurekaInstanceConfigBean
     * @param oldHost
     * @param newHost
     */
    private void updateHostInfo(EurekaInstanceConfigBean eurekaInstanceConfigBean, String oldHost, String newHost) {
        //更新实例
        eurekaInstanceConfigBean.setInstanceId(newHost + ":" + eurekaInstanceConfigBean.getNonSecurePort());

        if (StringUtils.hasText(eurekaInstanceConfigBean.getIpAddress())) {
            eurekaInstanceConfigBean.setIpAddress(eurekaInstanceConfigBean.getIpAddress().replaceAll(oldHost, newHost));
        }


        if (StringUtils.hasText(eurekaInstanceConfigBean.getStatusPageUrl())) {
            eurekaInstanceConfigBean.setStatusPageUrl(eurekaInstanceConfigBean.getStatusPageUrl().replaceAll(oldHost, newHost));
        }


        if (StringUtils.hasText(eurekaInstanceConfigBean.getHealthCheckUrl())) {
            eurekaInstanceConfigBean.setHealthCheckUrl(eurekaInstanceConfigBean.getHealthCheckUrl().replaceAll(oldHost, newHost));
        }

    }


}
