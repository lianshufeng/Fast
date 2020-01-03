package com.fast.dev.ucenter.security.resauth;

import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import com.fast.dev.ucenter.security.resauth.model.ResourceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.annotation.AnnotationMetadataExtractor;

import java.util.ArrayList;
import java.util.Collection;

public class ResourceAuthAnnotationMetadataExtractor implements AnnotationMetadataExtractor<ResourceAuth> {

    @Autowired
    private ResourcesAuthHelper resourcesAuthHelper;


    public Collection<ConfigAttribute> extractAttributes(ResourceAuth resourceAuth) {
        final String attributeToken = resourceAuth.value();
        String remark = resourceAuth.remark();
        ResourceInfo resourceInfo = ResourceInfo.builder().name(attributeToken).remark(remark).build();

        //添加资源到缓存里
        if (!this.resourcesAuthHelper.appendResourceInfo(resourceInfo)) {
            throw new RuntimeException("资源注解添加失败 : " + resourceInfo);
        }

        return new ArrayList<ConfigAttribute>() {
            {
                add(new SecurityConfig(attributeToken));
            }
        };
    }
}
