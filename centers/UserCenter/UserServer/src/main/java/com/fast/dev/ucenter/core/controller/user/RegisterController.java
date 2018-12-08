package com.fast.dev.ucenter.core.controller.user;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.service.UserService;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 * 用户注册的控制层
 */
@RestController
public class RegisterController extends SuperController {

    @Autowired
    private UserService userService;

    /**
     * 获取注册令牌
     *
     * @return
     */
    @RequestMapping("getRegisterToken")
    public Object getRegisterToken(UserLoginType loginType, String loginName, TokenEnvironment env) {
        Assert.notNull(loginType, "类型不能为空");
        Assert.hasText(loginName, "登陆账号不能为空");
        return InvokerResult.success(this.userService.getUserRegisterToken(loginType, loginName, env));
    }


    /**
     * 注册用户
     *
     * @param token
     * @param code
     * @param passWord
     * @return
     */
    @RequestMapping("register")
    public Object register(String token, String code, String passWord, TokenEnvironment env) {
        Assert.hasText(token, "令牌不能为空");
        Assert.hasText(code, "校验码不能为空");
        Assert.hasText(passWord, "密码不能为空");
        return InvokerResult.success(this.userService.register(env, token, code, passWord));
    }


}
