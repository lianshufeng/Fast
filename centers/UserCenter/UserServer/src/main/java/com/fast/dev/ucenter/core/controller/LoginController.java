package com.fast.dev.ucenter.core.controller;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.core.service.UserService;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public Object login(String token, String code, String passWord) {
        Assert.hasText(token, "令牌不能为空");
        Assert.hasText(code, "校验码不能为空");
        Assert.hasText(passWord, "密码不能为空");
        return InvokerResult.success(this.userService.login(token, code, passWord));
    }


    /**
     * 登陆
     *
     * @return
     */
    @RequestMapping("getLoginToken")
    public Object getLoginToken(UserLoginType loginType, String loginName) {
        Assert.notNull(loginType, "类型不能为空");
        Assert.hasText(loginName, "登陆账号不能为空");
        return InvokerResult.success(this.userService.getUserLoginToken(loginType, loginName, null));
    }





//    @RequestMapping("login.html")
//    public ModelAndView login() {
//        ModelAndView modelAndView = new ModelAndView("login.html");
//        modelAndView.addObject("name", System.currentTimeMillis());
//        return modelAndView;
//    }


}
