package com.fast.dev.ucenter.core.service;

import com.fast.dev.ucenter.core.model.UserTokenModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 功能解决
 */

public interface UserManagerService {


    @RequestMapping(value = "/ucenter/manager/queryUserToken", method = RequestMethod.POST)
    public UserTokenModel queryByUserToken(@RequestParam("token") String token);


    @RequestMapping(value = "/ucenter/manager/logout", method = RequestMethod.POST)
    public boolean logout(@RequestParam("token") String token);


}
