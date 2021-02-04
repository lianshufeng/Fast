package com.fast.dev.ucenter.security.resauth;

import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuths;
import com.fast.dev.ucenter.security.resauth.model.ResourceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.annotation.AnnotationMetadataExtractor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ResourceAuthAnnotationMetadataExtractor implements AnnotationMetadataExtractor<ResourceAuths> {

    @Autowired
    private ResourcesAuthHelper resourcesAuthHelper;


    public Collection<ConfigAttribute> extractAttributes(ResourceAuths resourceAuths) {
        Set<ConfigAttribute> ret = new HashSet<>();
        for (ResourceAuth resourceAuth : resourceAuths.value()) {
            ret.addAll(extractAttributes(resourceAuth));
        }
        return ret;
    }

    public Collection<ConfigAttribute> extractAttributes(ResourceAuth resourceAuth) {
        Set<ConfigAttribute> ret = new HashSet<>();
        for (String token : resourceAuth.value()) {
            String remark = resourceAuth.remark();
            ResourceInfo resourceInfo = ResourceInfo.builder().name(token).remark(remark).build();

            //添加资源到缓存里
            if (!this.resourcesAuthHelper.appendResourceInfo(resourceInfo)) {
                throw new RuntimeException("资源注解添加失败 : " + resourceInfo);
            }

            //注册到spring里
            extractAttributes(ret, resourceInfo);
        }
        return ret;
    }

    private void extractAttributes(Collection<ConfigAttribute> ret, ResourceInfo resourceInfo) {
        ret.add(new SecurityConfig(resourceInfo.getName()));
    }


//    @Override
//    public Collection<ConfigAttribute> extractAttributes(ResourceAuths resourceAuth) {
//        Set<ConfigAttribute> ret = new HashSet<>();
//        for (String token : resourceAuth.value()) {
//            String remark = resourceAuth.remark();
//            ResourceInfo resourceInfo = ResourceInfo.builder().name(token).remark(remark).build();
//
//            //添加资源到缓存里
//            if (!this.resourcesAuthHelper.appendResourceInfo(resourceInfo)) {
//                throw new RuntimeException("资源注解添加失败 : " + resourceInfo);
//            }
//
//            //注册到spring里
//            extractAttributes(ret, resourceInfo);
//        }
//        return ret;
//    }


}
