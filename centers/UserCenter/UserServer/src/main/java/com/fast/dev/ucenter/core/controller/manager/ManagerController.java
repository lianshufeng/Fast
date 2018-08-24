package com.fast.dev.ucenter.core.controller.manager;

import com.fast.dev.ucenter.core.service.UserManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("manager")
public class ManagerController {

    @Autowired
    private UserManagerService userManager;

    @RequestMapping("queryUserToken")
    public Object queryUserToken(String token) {
        Assert.hasText(token, "用户令牌不能为空");
        return this.userManager.queryByUserToken(token);
    }


}
