package com.fast.dev.auth.client.register.helper;

import com.fast.dev.auth.client.model.*;
import com.fast.dev.auth.client.register.AuthRegister;
import com.fast.dev.auth.client.register.EnterpriseInitEvent;
import com.fast.dev.auth.client.service.AuthNameService;
import com.fast.dev.auth.client.service.IdentityNameService;
import com.fast.dev.auth.client.service.RoleService;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.ucenter.security.resauth.ResourcesAuthHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuthRegisterHelper implements ApplicationRunner {


    @Autowired
    private ResourcesAuthHelper resourcesAuthHelper;

    @Autowired
    private AuthNameService authNameService;

    @Autowired
    private IdentityNameService identityNameService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RoleService roleService;


    //身份
    private Set<IdentityNameModel> identityNameModels = new HashSet<>();

    //权限名
    private Set<AuthNameModel> authNameModels = new HashSet<>();


    //企业初始化事件
    @Getter
    private Set<EnterpriseInitEvent> enterpriseInitEvents = new HashSet<>();


    private void init() {
        this.applicationContext.getBeansOfType(AuthRegister.class).values().forEach((it) -> {

            //添加身份
            it.addIdentity(this.identityNameModels);

            //添加权限
            it.addAuthName(this.authNameModels);

            //添加企业初始化事件
            it.addEnterpriseInitEvent(this.enterpriseInitEvents);

        });
    }


    /**
     * 程序启动自动注册
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(() -> {
            init();
            register();
        }).start();
    }


    /**
     * 企业被创建事件
     */
    public void enterpriseInit(EnterpriseModel enterpriseModel, String ownerUid) {
        //需要初始化的角色用户模型
        Set<RoleUserModel> roleUserModels = new HashSet<>();
        this.enterpriseInitEvents.forEach((it) -> {
            if (it.condition(enterpriseModel)) {
                Optional.ofNullable(it.onCreate(enterpriseModel, ownerUid)).ifPresent((roleUserModel) -> {
                    roleUserModels.addAll(roleUserModel);
                });
            }
        });

        // 仅有非空的情况才更新
        if (roleUserModels.size() > 0) {
            EnterpriseRoleUserModel enterpriseRoleUserModel = new EnterpriseRoleUserModel();
            enterpriseRoleUserModel.setEnterpriseId(enterpriseModel.getId());
            enterpriseRoleUserModel.setRoleUserModels(roleUserModels);

            //更新企业用户信息
            ResultContent resultContent = this.roleService.updateEnterpriseRoleUser(enterpriseRoleUserModel);

            log.info("企业初始化 : {} -> {}", JsonUtil.toJson(enterpriseRoleUserModel), JsonUtil.toJson(resultContent));
        }


    }


    /**
     * 注册，如果手动注册可调用此方法
     */
    public void register() {
        //注册权限名
        registerAuthName();

        //注册身份
        registerIdentity();

    }

    /**
     * 注册身份
     */
    private void registerIdentity() {
        this.identityNameService.add(this.identityNameModels.toArray(new IdentityNameModel[0]));
    }


    /**
     * 注册权限名
     */
    private void registerAuthName() {
        this.authNameService.add(this.authNameModels.toArray(new AuthNameModel[0]));
    }


}
