package com.fast.dev.ucenter.security.service;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.security.model.UserAuth;
import com.fast.dev.ucenter.security.model.UserIdentity;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import com.fast.dev.ucenter.security.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * 远程用户中心的接口
 */
@Slf4j
public class UserCenterService {


    @Autowired
    private RemoteUserCenterService remoteUserCenterService;


    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private ApplicationContext applicationContext;


    private UserAuthentication userAuthentication;

    @PostConstruct
    private void init() {
        Collection<UserAuthentication> collection = this.applicationContext.getBeansOfType(UserAuthentication.class).values();
        if (collection != null && collection.size() > 0) {
            this.userAuthentication = collection.toArray(new UserAuthentication[0])[0];
        }

        if (this.userAuthentication == null) {
            throw new RuntimeException("用户权限模块，未发现实现的 UserAuthentication 接口 ");
        }

    }


    /**
     * 查询用户中心的令牌信息
     *
     * @param token
     * @return
     */
    public UserAuth query(String token) {
        if (StringUtils.isEmpty(token) || "null".equalsIgnoreCase(token)) {
            log.info("[Token_Null] : " + token);
            return null;
        }
        UserTokenModel userTokenModel = queryByUserToken(token);
        log.debug("[RemoteQueryByUserToken] : {}", token + " -> " + JsonUtil.toJson(userTokenModel));
        if (userTokenModel == null) {
            return null;
        }
        //成功返回角色列表
        UserIdentity userIdentity = this.userAuthentication.authentication(userTokenModel);
        log.debug("[authentication] : {}", userIdentity);
        if (userIdentity == null) {
            return null;
        }
        UserAuth userAuth = new UserAuth();
        //拷贝令牌信息
        BeanUtils.copyProperties(userTokenModel, userAuth);
        //拷贝权限信息
        BeanUtils.copyProperties(userIdentity, userAuth);
        //设置创建时间
        userAuth.setCreateTime(TimeUtil.getTime());
        return userAuth;
    }


    /**
     * 通过用户令牌查询用户信息
     *
     * @param token
     * @return
     */
//    private UserTokenModel queryByUserToken(String token) {
//        UserTokenModel userTokenModel = this.remoteUserCenterService.queryByUserToken(token);
//        if (userTokenModel == null && token.length() == 32) {
//            for (int i = 0; i < 3; i++) {
//                userTokenModel = this.remoteUserCenterService.queryByUserToken(token);
//                if (userTokenModel != null) {
//                    break;
//                }
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return userTokenModel;
//    }

    /**
     * 通过用户令牌查询用户信息
     *
     * @param token
     * @return
     */
    private UserTokenModel queryByUserToken(String token) {
        return this.remoteUserCenterService.queryByUserToken(token);
    }

}
