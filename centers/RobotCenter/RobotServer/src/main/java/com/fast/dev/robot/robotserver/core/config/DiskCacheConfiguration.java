package com.fast.dev.robot.robotserver.core.config;

import com.fast.dev.core.helper.DiskCacheStoreHelper;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class DiskCacheConfiguration {


    //最大的缓存数量
    private final static long maxCacheSize = 1024 * 1024 * 1024 * 1;


    @Bean("dbDiskCache")
    public DiskCacheStoreHelper dbDiskCache() {
        return DiskCacheStoreHelper.build(new File(new ApplicationHome(DiskCacheConfiguration.class).getDir().getAbsolutePath() + "/_dbDiskCache"), maxCacheSize);
    }


    @Bean("robotDiskCache")
    public DiskCacheStoreHelper robotDiskCache() {
        return DiskCacheStoreHelper.build(new File(new ApplicationHome(DiskCacheConfiguration.class).getDir().getAbsolutePath() + "/_robotDiskCache"), maxCacheSize);
    }


}
