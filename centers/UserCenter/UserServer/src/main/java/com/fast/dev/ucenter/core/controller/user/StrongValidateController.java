package com.fast.dev.ucenter.core.controller.user;


import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 强化令牌
 */
@RestController
public class StrongValidateController extends SuperController {


    @Autowired
    private UserService userService;


    @RequestMapping("strongToken")
    public Object strongToken(String token, String code) {
        Assert.hasText(token, "令牌不能为空");
        Assert.hasText(code, "校验码不能为空");
        return InvokerResult.success(this.userService.strongToken(token, code));
    }


}
