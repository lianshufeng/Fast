package com.fast.dev.auth.client.register;

import com.fast.dev.auth.client.model.AuthNameModel;
import com.fast.dev.auth.client.model.IdentityNameModel;
import com.fast.dev.ucenter.security.resauth.ResourcesAuthHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AuthRegister {

    @Autowired
    private ResourcesAuthHelper resourcesAuthHelper;

    /**
     * 增加权限名
     */
    public abstract void addIdentity(Set<IdentityNameModel> identityNameModels);


    /**
     * 添加到权限名
     *
     * @param authNameModels
     */
    public void addAuthName(Set<AuthNameModel> authNameModels) {
        Optional.of(this.resourcesAuthHelper.getResourceInfos()).ifPresent((resourceInfos) -> {
            authNameModels.addAll(resourceInfos.parallelStream().map((resourceInfo) -> {
                AuthNameModel authNameModel = new AuthNameModel();
                BeanUtils.copyProperties(resourceInfo, authNameModel);
                return authNameModel;
            }).collect(Collectors.toSet()));
        });
    }


    /**
     * 添加企业初始化事件
     *
     * @param enterpriseInitEvents
     */
    public abstract void addEnterpriseInitEvent(Set<EnterpriseInitEvent> enterpriseInitEvents);

}
