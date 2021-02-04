package com.fast.dev.ucenter.security.resauth;


import com.fast.dev.ucenter.security.resauth.model.ResourceInfo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 资源权限注解
 */

@Log
public class ResourcesAuthHelper {


    @Autowired
    private ApplicationContext applicationContext;


    private Map<String, ResourceInfo> resourceInfoMap = null;


    @PostConstruct
    private void init() {
        resourceInfoMap = new ConcurrentHashMap<>();
    }


    /**
     * 获取资源注解的权限描述
     *
     * @return
     */
    public Collection<ResourceInfo> getResourceInfos() {
        return getResourceInfos(null);
    }


    /**
     * 获取资源信息
     *
     * @param exclude
     * @return
     */
    public Collection<ResourceInfo> getResourceInfos(Set<String> exclude) {
        Set<ResourceInfo> sets = new HashSet<>();
        resourceInfoMap.values().forEach((it) -> {
            if (exclude == null || exclude.size() == 0) {
                sets.add(it);
            } else if (!exclude.contains(it.getName())) {
                sets.add(it);
            }
        });
        return sets;
    }


    /**
     * 追加方法
     *
     * @param methodInfo
     */
    protected boolean appendResourceInfo(ResourceInfo methodInfo) {
        if (StringUtils.hasText(methodInfo.getRemark())) {
            this.resourceInfoMap.put(methodInfo.getName(), methodInfo);
        } else if (!this.resourceInfoMap.containsKey(methodInfo.getName())) {
            this.resourceInfoMap.put(methodInfo.getName(), methodInfo);
        }
        return true;
    }


}
