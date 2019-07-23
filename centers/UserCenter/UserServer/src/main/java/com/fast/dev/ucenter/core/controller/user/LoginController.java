package com.fast.dev.ucenter.core.controller.user;

import com.fast.dev.core.util.net.IPUtil;
import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.core.model.ClientInfo;
import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.service.UserService;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

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
    public Object login(HttpServletRequest request, String token, String code, String passWord, @RequestParam(defaultValue = "604800000") Long expireTime, TokenEnvironment env) {
        Assert.hasText(token, "令牌不能为空");
        Assert.hasText(code, "校验码不能为空");
        Assert.hasText(passWord, "密码不能为空");
        env.setClientInfo(new ClientInfo(IPUtil.getRemoteIp(request), request.getHeader("User-Agent")));
        return InvokerResult.success(this.userService.login(env, token, code, passWord, expireTime));
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
    public Object logout(String _uToken, Boolean all) {
        Assert.hasText(_uToken, "用户令牌不能为空");
        if (all == null) {
            all = false;
        }
        return InvokerResult.success(this.userService.logout(_uToken, all));
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


    @RequestMapping("login.html")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login.html");
        modelAndView.addObject("name", System.currentTimeMillis());
        return modelAndView;
    }


}
