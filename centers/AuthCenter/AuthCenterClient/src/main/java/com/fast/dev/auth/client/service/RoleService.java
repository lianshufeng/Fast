package com.fast.dev.auth.client.service;

import com.fast.dev.auth.client.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "authcenterserver/manager/role")
public interface RoleService {


    /**
     * 更新企业角色用户
     *
     * @return
     */
    @RequestMapping(value = "updateEnterpriseRoleUser")
    ResultContent updateEnterpriseRoleUser(@RequestBody EnterpriseRoleUserModel enterpriseRoleUserModel);


    /**
     * 更新角色
     *
     * @param roleModel
     * @return
     */
    @RequestMapping(value = "updateRole", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent updateRole(@ModelAttribute RoleModel roleModel);


    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    @RequestMapping(value = "removeRole", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent removeRole(@RequestParam("roleId") String roleId);


    /**
     * 查询角色
     *
     * @param roleId
     * @return
     */
    @RequestMapping(value = "get", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent<RoleModel> get(@RequestParam("roleId") String roleId);


    /**
     * 分页条件查询企业下的角色
     *
     * @param roleModel
     * @param pageable
     * @return
     */
    @RequestMapping(value = "list", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    Page<RoleModel> list(@ModelAttribute RoleModel roleModel, @PageableDefault Pageable pageable);


    /**
     * 查询该企业下的所有角色
     *
     * @param enterpriseId
     * @return
     */
    @RequestMapping(value = "listRoles", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    List<RoleModel> listRoles(@RequestParam("enterpriseId") String enterpriseId);


    /**
     * 通过角色名查询
     *
     * @param enterpriseId
     * @param roleName
     * @return
     */
    @RequestMapping(value = "findByRoleName", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    RoleModel findByRoleName(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("roleName") String roleName);


    /**
     * 通过权限名查询角色列表
     *
     * @param enterpriseId
     * @param authName
     * @return
     */
    @RequestMapping(value = "findByAuthName", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    List<RoleModel> findByAuthName(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("authName") String authName);


    /**
     * 通过身份明查询角色列表
     *
     * @param enterpriseId
     * @param identity
     * @return
     */
    @RequestMapping(value = "findByIdentity", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    List<RoleModel> findByIdentity(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("identity") String... identity);


    /**
     * 角色里添加用户
     *
     * @param roleId
     * @param uid
     * @return
     */
    @RequestMapping(value = "addUserRole", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent addUserRole(@RequestParam("roleId") String roleId, @RequestParam("uid") String... uid);


    /**
     * 角色里删除用户
     *
     * @param roleId
     * @param userIds
     * @return
     */
    @RequestMapping(value = "removeUserRole", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    ResultContent removeUserRole(@RequestParam("roleId") String roleId, @RequestParam("uid") String... userIds);


    /**
     * 查询角色中的所有成员
     *
     * @param roleId
     * @param pageable
     * @return
     */
    @RequestMapping(value = "listRoleUser", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE})
    Page<UserModel> listRoleUser(@RequestParam("roleId") String[] roleId, @PageableDefault Pageable pageable);


    /**
     * MQL分页条件查询角色列表
     *
     * @param userQueryModel
     * @param pageable
     * @return
     */
    @RequestMapping(value = "queryRole", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.ALL_VALUE}, method = RequestMethod.POST)
    Page<RoleModel> queryRole(@ModelAttribute UserQueryModel userQueryModel, @PageableDefault Pageable pageable);

}
