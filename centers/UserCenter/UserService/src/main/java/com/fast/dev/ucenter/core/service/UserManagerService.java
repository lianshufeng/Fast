package com.fast.dev.ucenter.core.service;

import com.fast.dev.ucenter.core.model.UserTokenModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 功能解决
 */

public interface UserManagerService {


    /**
     * 查询完成的用户令牌
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/ucenter/manager/queryUserToken")
    public UserTokenModel queryByUserToken(String token);


}
