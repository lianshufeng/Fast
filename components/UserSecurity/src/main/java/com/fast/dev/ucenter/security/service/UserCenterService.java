package com.fast.dev.ucenter.security.service;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.core.service.UserManagerService;
import com.fast.dev.ucenter.security.model.UserAuthenticationModel;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 远程用户中心的接口
 */
public class UserCenterService {


    @Autowired
    private RemoteUserCenterService remoteUserCenterService;

    /**
     * 查询用户中心的令牌信息
     *
     * @param token
     * @return
     */
    public UserAuthenticationModel query(String token) {
        System.out.println(token);
        UserTokenModel userTokenModel = this.remoteUserCenterService.queryByUserToken(token);
        System.out.println(JsonUtil.toJson(userTokenModel));

        return null;
    }

}
