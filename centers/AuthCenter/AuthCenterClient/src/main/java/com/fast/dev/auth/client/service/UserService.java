package com.fast.dev.auth.client.service;

import com.fast.dev.auth.client.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@FeignClient(name = "authcenterserver/manager/user")
public interface UserService {

    /**
     * 更新用户信息
     *
     * @param enterpriseId
     * @param userModel
     */
    @RequestMapping(value = "updateUser", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent updateUser(@RequestParam("enterpriseId") String enterpriseId, @ModelAttribute UserModel userModel);


    /**
     * 查询用户
     *
     * @param enterpriseId
     * @param uid
     * @return
     */
    @RequestMapping(value = "getUser", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<UserModel> getUser(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("uid") String uid);


    /**
     * 获取该用户所用户的线索的企业 : 登录过一次就算
     *
     * @param uid
     * @return
     */
    @RequestMapping(value = "getEnterprise", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<Page<EnterpriseModel>> getEnterprise(@RequestParam("uid") String uid, @PageableDefault Pageable pageable);


    /**
     * 获取附属的企业： 用户在该企业里
     *
     * @param uid
     * @return
     */
    @RequestMapping(value = "getAffiliatedEnterprises", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<List<EnterpriseModel>> getAffiliatedEnterprises(@RequestParam("uid") String uid);


    /**
     * 分页条件查询企业用户
     *
     * @param mql
     * @param pageable
     * @return
     */
//    @Deprecated
    @RequestMapping(value = "queryEnterpriseUser", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    Page<EnterpriseUserModel> queryEnterpriseUser(@RequestParam("mql") String mql, @PageableDefault Pageable pageable);


    /**
     * 查询企业模型
     *
     * @param userQueryModel
     * @param pageable
     * @return
     */
    @RequestMapping(value = "queryUser", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE}, method = RequestMethod.POST)
    Page<EnterpriseUserModel> queryUser(@ModelAttribute UserQueryModel userQueryModel, @PageableDefault Pageable pageable);


    /**
     * 查询用户info表的所有key
     *
     * @return
     */
    @RequestMapping(value = "getUserInfoKey", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<Set<String>> getUserInfoKey();

}
