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

/**
 * 密码控制器
 */
@RestController
public class PassWordController extends SuperController {

    @Autowired
    private UserService userService;

    /**
     * 获取修改密码的令牌
     *
     * @param loginType
     * @param loginName
     * @param env
     * @return
     */
    @RequestMapping("getUpdatePassWordToken")
    public Object getUpdatePassWordToken(UserLoginType loginType, String loginName, TokenEnvironment env) {
        Assert.notNull(loginType, "类型不能为空");
        Assert.hasText(loginName, "登陆账号不能为空");
        return InvokerResult.success(this.userService.getUpdatePassWordToken(loginType, loginName, env));
    }


    /**
     * 修改密码
     *
     * @param token
     * @param code
     * @param passWord
     * @param newPassWord
     * @param env
     * @return
     */
    @RequestMapping("updatePassWord")
    public Object updatePassWord(String token, String code, String passWord, String newPassWord, TokenEnvironment env) {
        //旧密码可为空，满足快捷登陆的情况
        Assert.hasText(token, "令牌不能为空");
        Assert.hasText(code, "校验码不能为空");
        Assert.hasText(newPassWord, "新密码不能为空");
        return InvokerResult.success(this.userService.updatePassWord(token,code,passWord,newPassWord,env));
    }


}
