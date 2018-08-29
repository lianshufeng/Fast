package com.fast.dev.ucenter.core.controller.user;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.service.UserService;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController extends SuperController {

    @Autowired
    private UserService userService;


    /**
     * 登陆
     *
     * @return
     */
    @RequestMapping("login")
    public Object login(String token, String code, String passWord, @RequestParam(defaultValue = "604800000") Long timeOut) {
        Assert.hasText(token, "令牌不能为空");
        Assert.hasText(code, "校验码不能为空");
        Assert.hasText(passWord, "密码不能为空");
        return InvokerResult.success(this.userService.login(token, code, passWord, timeOut));
    }


    /**
     * 获取登陆令牌
     *
     * @return
     */
    @RequestMapping("getLoginToken")
    public Object getLoginToken(UserLoginType loginType, String loginName, TokenEnvironment env) {
        Assert.notNull(loginType, "类型不能为空");
        Assert.hasText(loginName, "登陆账号不能为空");
        return InvokerResult.success(this.userService.getUserLoginToken(loginType, loginName, env));
    }


    @RequestMapping("logout")
    public Object logout(String _uToken) {
        Assert.hasText(_uToken, "用户令牌不能为空");
        return InvokerResult.success(this.userService.logout(_uToken));
    }


    /**
     * 测试失效没
     *
     * @param _uToken
     * @return
     */
    @RequestMapping("ping")
    public Object ping(String _uToken) {
        Assert.notNull(_uToken, "令牌不能为空");
        return InvokerResult.success(this.userService.ping(_uToken));
    }


//    @RequestMapping("login.html")
//    public ModelAndView login() {
//        ModelAndView modelAndView = new ModelAndView("login.html");
//        modelAndView.addObject("name", System.currentTimeMillis());
//        return modelAndView;
//    }


}
