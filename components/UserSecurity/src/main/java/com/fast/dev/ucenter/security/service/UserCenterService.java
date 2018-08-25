package com.fast.dev.ucenter.security.service;

import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.security.model.UserAuthenticationModel;
import com.fast.dev.ucenter.security.model.UserIdentity;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * 远程用户中心的接口
 */
public class UserCenterService {


    @Autowired
    private RemoteUserCenterService remoteUserCenterService;


    @Autowired
    private ApplicationContext applicationContext;

    private UserAuthentication userAuthentication;

    @PostConstruct
    private void init() {
        Collection<UserAuthentication> collection = this.applicationContext.getBeansOfType(UserAuthentication.class).values();
        if (collection != null && collection.size() > 0) {
            userAuthentication = collection.toArray(new UserAuthentication[0])[0];
        }
    }


    /**
     * 查询用户中心的令牌信息
     *
     * @param token
     * @return
     */
    public UserAuthenticationModel query(String token) {
        UserTokenModel userTokenModel = this.remoteUserCenterService.queryByUserToken(token);
        if (userTokenModel == null || this.userAuthentication == null) {
            return null;
        }
        //成功返回角色列表
        UserIdentity userIdentity = this.userAuthentication.authentication(userTokenModel);
        if (userIdentity == null) {
            return null;
        }
        UserAuthenticationModel userAuthenticationModel = new UserAuthenticationModel();
        BeanUtils.copyProperties(userTokenModel,userAuthenticationModel);
        BeanUtils.copyProperties(userIdentity,userAuthenticationModel);
        return userAuthenticationModel;
    }

}
