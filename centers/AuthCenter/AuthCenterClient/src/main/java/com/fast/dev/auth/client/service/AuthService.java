package com.fast.dev.auth.client.service;

import com.fast.dev.auth.client.bean.AuthUser;
import com.fast.dev.auth.client.model.ResultContent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;


@FeignClient(name = "authcenterserver/manager/user/auth")
public interface AuthService {

    /**
     * 查询用户的默认权限
     *
     * @param uid
     * @return
     */
    @RequestMapping(value = "queryUserAuth", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<Set<String>> queryUserAuth(@RequestParam("uid") String uid);


    /**
     * 查询指定用户在企业下的权限
     *
     * @return
     */
    @RequestMapping(value = "queryUserEnterPrise", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<AuthUser> queryUserEnterPrise(@RequestParam("uid") String uid, @RequestParam("enterpriseId") String enterpriseId);


}
