package com.example.applicationdemo.core.controller;

import com.example.applicationdemo.core.model.TestModel;
import com.example.applicationdemo.core.model.UserModel;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.core.batch.BatchUserService;
import com.fast.dev.ucenter.core.model.batch.BatchQueryLoginNameModel;
import com.fast.dev.ucenter.core.model.batch.BatchQueryValue;
import com.fast.dev.ucenter.core.type.UserLoginType;
import com.fast.dev.ucenter.security.helper.UserHelper;
import com.fast.dev.ucenter.security.resauth.ResourcesAuthHelper;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 作者：练书锋
 * 时间：2018/8/24
 */
@Slf4j
// 支持动态刷新配置
@RefreshScope

@RestController
public class TestUser {


    @Autowired
    private RemoteUserCenterService remoteUserCenterService;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private UserHelper userHelper;

    @Autowired
    private ResourcesAuthHelper resourcesAuthHelper;


    @Autowired
    private BatchUserService batchUserService;


    //    @Value("${config.test2}")
    @Value("${server.port}")
    private String config;


    @RequestMapping("config")
    public Object config() {
        return InvokerResult.success(config);
    }


    @RequestMapping("ping")
//    @PreAuthorize("hasRole('AUTH_USER')")
//    @Secured({"test","AUTH_USER"})
    @Secured({"user"})
    public Object ping() {
        System.out.println("user:" + JsonUtil.toJson(userHelper.getUser()));
        return InvokerResult.success("ping");
    }


    /**
     * 批量查询用户id
     *
     * @param uid
     * @return
     */
    @RequestMapping("batchUid")
    public Object batchUid(String[] uid) {
        return this.batchUserService.queryUserId(BatchQueryValue.builder().values(Set.of(uid)).build());
    }

    @RequestMapping("batchPhone")
    public Object batchPhone(String[] phone) {
        return this.batchUserService.queryByLoginName(BatchQueryLoginNameModel.builder().items(
                Arrays.stream(phone).map((p) -> {
                    return BatchQueryLoginNameModel.LoginItem.builder().loginType(UserLoginType.Phone).loginName(p).build();
                }).collect(Collectors.toList()).toArray(new BatchQueryLoginNameModel.LoginItem[0])
        ).build());
    }


    /**
     * 测试
     *
     * @return
     */
    @RequestMapping("/**/1.html")
    public Object test333(TestModel test) {
        return test;
    }



    /**
     * 测试
     *
     * @return
     */
    @RequestMapping("test")
    public Object test(TestModel test) {
        return test;
    }


    @RequestMapping("excpiton")
    public Object excpiton(String info) {
        Assert.hasText(info, "不能为空");
        System.out.println("user:" + JsonUtil.toJson(userHelper.getUser()));
        return InvokerResult.success("login");
    }

    @RequestMapping("logout")
    @ResourceAuth(value = "logout", remark = "注销")
    public Object logout() {
        System.out.println("user:" + JsonUtil.toJson(userHelper.getUser()));
        //调用管理模块进行通知注销
        this.remoteUserCenterService.logout(userHelper.getUser().getuToken());
        return InvokerResult.success("logout");
    }


    /**
     * 直接传json对象，但需要设置head，优点是，不需要考虑表单name的顺序，数据签名或加密容易，缺点是需要客户端稍作配置
     * <p>
     * head  { Content-Type : application/json}
     *
     * @param userModel
     * @return
     */
    @RequestMapping("json")
    public Object json(@RequestBody UserModel userModel) {
        log.info("json : {}", JsonUtil.toJson(userModel));
        return userModel;
    }


    /**
     * mvc 支持model 接收参数
     *
     * @param userModel
     * @return
     */
    @RequestMapping("model")
    public Object model(UserModel userModel) {
        log.info("model : {}", JsonUtil.toJson(userModel));
        return userModel;
    }


    @RequestMapping("auth1")
    @ResourceAuth(value = "auth1", remark = "权限1")
    public Object auth1() {
        System.out.println("auth1 : -->> ");
        return "";
    }

    @RequestMapping("auth2")
    @ResourceAuth(value = "auth2", remark = "权限2")
    public Object auth2() {
        System.out.println("auth2 : -->> ");
        return "";
    }


    /**
     * 查询系统内所有的权限注解
     *
     * @return
     */
    @RequestMapping("getAllAuths")
    public Object getAllAuths() {
        return resourcesAuthHelper.getResourceInfos();
    }


}
