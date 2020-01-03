package com.fast.dev.user.auth.helper;

import com.fast.dev.ucenter.security.helper.UserHelper;
import com.fast.dev.ucenter.security.model.UserAuth;
import com.fast.dev.user.auth.model.DetailsRoleModel;
import com.fast.dev.user.auth.model.IdentityModel;
import com.fast.dev.user.auth.service.IdentityRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IdentityHelper {

    private Map<String, IdentityModel> identitys = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserHelper userHelper;


    @Autowired
    private void init() {
        Set<IdentityModel> identityModels = new HashSet<>();
        for (IdentityRegister value : this.applicationContext.getBeansOfType(IdentityRegister.class).values()) {
            //注册身份
            value.register(identityModels);
        }

        //更新身份信息
        identityModels.forEach(it -> {
            if (!identitys.containsKey(it.getName()) || !StringUtils.hasText(it.getRemark())) {
                identitys.put(it.getName(), it);
            }
        });
    }


    /**
     * 获取所有的身份及描述
     *
     * @return
     */
    public Map<String, IdentityModel> getIdentitys() {
        return new HashMap<>(this.identitys);
    }


    /**
     * 获取当前用户的所有身份
     *
     * @return
     */
    public Set<IdentityModel> currentUserIdentity() {
        UserAuth userAuth = this.userHelper.getUser();
        if (userAuth == null) {
            return null;
        }
        Object roles = userAuth.getDetails().get("role");
        if (roles instanceof List) {
            List<DetailsRoleModel> role = (List) roles;
            Set<IdentityModel> sets = new HashSet<>();
            role.forEach((it) -> {
                String identity = it.getIdentity();
                if (StringUtils.hasText(identity)) {
                    IdentityModel identityModel = this.identitys.get(identity);
                    if (identityModel != null) {
                        sets.add(identityModel);
                    }
                }
            });

            return sets;
        }
        return null;
    }


    /**
     * 当前用户是否有此权限
     *
     * @param identity
     * @return
     */
    public boolean currentUserContainsIdentity(String... identity) {
        Set<IdentityModel> identityModels = currentUserIdentity();
        if (identityModels == null) {
            return false;
        }
        if (identity == null) {
            return false;
        }


        for (IdentityModel it : identityModels) {
            if (Arrays.binarySearch(identity,it.getName())> -1){
                return true;
            }
        }
        return false;
    }


}
