package com.example.applicationdemo.core.controller;

import com.example.applicationdemo.core.model.UserModel;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.security.helper.UserHelper;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;

/**
 * 作者：练书锋
 * 时间：2018/8/24
 */
@Slf4j
@RestController
public class TestUser {


    @Autowired
    private RemoteUserCenterService remoteUserCenterService;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private UserHelper userHelper;


    @RequestMapping("ping")
//    @PreAuthorize("hasRole('AUTH_USER')")
//    @Secured({"test","AUTH_USER"})
    @Secured({"user"})
    public Object ping() {
        System.out.println("user:" + JsonUtil.toJson(userHelper.getUser()));
        return InvokerResult.success("ping");
    }


    @RequestMapping("test")
    public Object test() {
        MultiValueMap<String, Object> m = new LinkedMultiValueMap<>();
        m.put("loginType", new ArrayList<Object>(){
            {
                add("Phone");
            }
        });
        m.put("loginName",new ArrayList<Object>(){{
            add("15123241353");
        }});
        return restTemplate.postForEntity("http://USERSERVER/ucenter/user/getRegisterToken", m, Object.class).getBody();
    }


    @RequestMapping("excpiton")
    public Object excpiton(String info) {
        Assert.hasText(info, "不能为空");
        System.out.println("user:" + JsonUtil.toJson(userHelper.getUser()));
        return InvokerResult.success("login");
    }

    @RequestMapping("logout")
    @Secured({"user"})
    public Object logout() {
        System.out.println("user:" + JsonUtil.toJson(userHelper.getUser()));
        //调用管理模块进行通知注销
        this.remoteUserCenterService.logout(userHelper.getUser().getuToken());
        return InvokerResult.success("logout");
    }


    /**
     * 直接传json对象，但需要设置head，优点是，不需要考虑表单name的顺序，数据签名或加密容易，缺点是需要客户端稍作配置
     *
     * head  { Content-Type : application/json}
     * @param userModel
     * @return
     */
    @RequestMapping("json")
    public Object json(@RequestBody UserModel userModel){
        log.info("json : {}",JsonUtil.toJson(userModel));
        return userModel;
    }


    /**
     *  mvc 支持model 接收参数
     * @param userModel
     * @return
     */
    @RequestMapping("model")
    public Object model(UserModel userModel){
        log.info("model : {}",JsonUtil.toJson(userModel));
        return userModel;
    }

}
