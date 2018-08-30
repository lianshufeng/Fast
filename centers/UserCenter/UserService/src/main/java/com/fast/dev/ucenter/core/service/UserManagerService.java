package com.fast.dev.ucenter.core.service;

import com.fast.dev.ucenter.core.model.UserTokenModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 功能解决
 */

public interface UserManagerService {


    /**
     * 通过令牌查询详情
     * @param token
     * @return
     */
    @RequestMapping(value = "/manager/queryUserToken", method = RequestMethod.POST)
    UserTokenModel queryByUserToken(@RequestParam("token") String token);


    /**
     * 注销
     * @param token
     * @return
     */
    @RequestMapping(value = "/manager/logout", method = RequestMethod.POST)
    boolean logout(@RequestParam("token") String token);


}
