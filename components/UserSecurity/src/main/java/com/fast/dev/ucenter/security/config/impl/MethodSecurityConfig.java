package com.fast.dev.ucenter.security.config.impl;

import com.fast.dev.ucenter.security.conf.UserSecurityConf;
import com.fast.dev.ucenter.security.resauth.ResourceAuthAnnotationMetadataExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.annotation.SecuredAnnotationSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import javax.annotation.Resource;

/**
 * 角色选取控制器
 */
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true, proxyTargetClass = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Resource
    private UserSecurityConf userSecurityConfig;

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        AffirmativeBased accessDecisionManager = (AffirmativeBased) super.accessDecisionManager();

        //Remove the ROLE_ prefix from RoleVoter for @Secured and hasRole checks on methods
        accessDecisionManager.getDecisionVoters().stream()
                .filter(RoleVoter.class::isInstance)
                .map(RoleVoter.class::cast)
                .forEach(it -> it.setRolePrefix(userSecurityConfig.getRolePrefixName()));

        return accessDecisionManager;
    }


//    customMethodSecurityMetadataSource


    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return new SecuredAnnotationSecurityMetadataSource(resourceAuthAnnotationMetadataExtractor());
    }

    @Bean
    ResourceAuthAnnotationMetadataExtractor resourceAuthAnnotationMetadataExtractor() {
        return new ResourceAuthAnnotationMetadataExtractor();
    }

}

