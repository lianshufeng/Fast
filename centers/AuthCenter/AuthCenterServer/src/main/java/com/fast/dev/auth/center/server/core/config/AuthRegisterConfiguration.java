package com.fast.dev.auth.center.server.core.config;

import com.fast.dev.auth.center.server.core.register.EnterpriseInitEventImpl;
import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.model.IdentityNameModel;
import com.fast.dev.auth.client.register.AuthRegister;
import com.fast.dev.auth.client.register.EnterpriseInitEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class AuthRegisterConfiguration extends AuthRegister {

    @Override
    public void addIdentity(Set<IdentityNameModel> identityNameModels) {
        identityNameModels.addAll(Arrays.stream(ResourceAuthConstant.authConstantTypes).map((it) -> {
            IdentityNameModel identityNameModel = new IdentityNameModel();
            identityNameModel.setName(it.name());
            identityNameModel.setRemark(it.getIdentity());
            return identityNameModel;
        }).collect(Collectors.toSet()));
    }

    @Override
    public void addEnterpriseInitEvent(Set<EnterpriseInitEvent> enterpriseInitEvents) {
        enterpriseInitEvents.add(enterpriseInitEvent());
    }


    @Bean
    public EnterpriseInitEvent enterpriseInitEvent() {
        return new EnterpriseInitEventImpl();
    }
}

