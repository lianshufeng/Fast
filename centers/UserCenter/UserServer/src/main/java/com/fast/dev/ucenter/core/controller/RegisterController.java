package com.fast.dev.ucenter.core.controller;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.core.service.UserService;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

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
    public Object getRegisterToken(UserLoginType type, String loginName) {
        Assert.notNull(type, "类型不能为空");
        return InvokerResult.success(this.userService.getUserRegisterToken(null, type, loginName));
    }


}
