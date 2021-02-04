package com.fast.dev.ucenter.core.controller.user;

import com.fast.dev.core.util.net.IPUtil;
import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.core.model.ClientInfo;
import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 作者：练书锋
 * 时间：2018/8/31
 */
@RestController
public class FastController extends SuperController {

    @Autowired
    private UserService userService;


    /**
     * 获取登陆令牌
     *
     * @return
     */
    @RequestMapping("getFastToken")
    public Object getLoginToken(String phone, TokenEnvironment env) {
        Assert.hasText(phone, "手机号码不能为空");
        return InvokerResult.success(this.userService.getFastToken(phone, env));
    }

    /**
     * 登陆
     *
     * @return
     */
    @RequestMapping("fast")
    public Object fast(HttpServletRequest request, String token, String code, @RequestParam(defaultValue = "604800000") Long expireTime, TokenEnvironment env) {
        Assert.hasText(token, "令牌不能为空");
        Assert.hasText(code, "校验码不能为空");
        env.setClientInfo(new ClientInfo(IPUtil.getRemoteIp(request), request.getHeader("User-Agent")));
        return InvokerResult.success(this.userService.fast(env, token, code, expireTime));
    }


}
