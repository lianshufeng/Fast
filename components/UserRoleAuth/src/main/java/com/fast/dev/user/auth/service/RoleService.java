package com.fast.dev.user.auth.service;

import com.fast.dev.user.auth.model.DetailsRoleModel;
import com.fast.dev.user.auth.model.RoleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface RoleService {


    /**
     * 更新角色
     *
     * @param roleModel
     * @return
     */
    boolean updateRole(RoleModel roleModel);


    /**
     * 设置角色继承关系
     *
     * @param roleName
     * @param parentRoleName
     * @return
     */
    boolean setRoleParent(String roleName, String parentRoleName);


    /**
     * 移除角色的父类
     *
     * @param roleName
     * @return
     */
    boolean removeRoleParent(String roleName);


    /**
     * 删除校色
     */
    boolean removeRole(String roleName);


    /**
     * 获取所有的校色列表
     *
     * @return
     */
    Page<RoleModel> listRole(String roleName, Pageable pageable);


    /**
     * 设置角色权限
     *
     * @param roleName
     * @param authName
     */
    boolean setRoleAuth(String roleName, String... authName);


    /**
     * 获取角色详情
     *
     * @param roleName
     * @return
     */
    DetailsRoleModel detailsRole(String roleName);


    /**
     * 获取角色权限
     *
     * @return
     */
    Set<String> getRoleAuth(String roleName);


    /**
     * 角色中添加用户
     *
     * @param roleName
     * @param userId,该 userid为当前系统的user的id， 非用户中心的id
     */
    long addUserRole(String roleName, String... userId);


    /**
     * 删除角色中的用户
     *
     * @param roleName
     * @param userId   该 userid为当前系统的user的id， 非用户中心的id
     */
    long removeUserRole(String roleName, String... userId);


    /**
     * 获取角色中的用户
     *
     * @param roleName
     * @return
     */
    Page<Object> listRoleUser(String roleName, Pageable pageable);


    /**
     * 获取用户有哪些角色
     *
     * @param pageable
     * @return
     */
    Page<RoleModel> listUserRole(String userId, Pageable pageable);


    /**
     * 设置角色上的身份
     *
     * @param roleName
     * @return
     */
    boolean setRoleIdentity(String roleName, String identity);


    /**
     * 通过身份查询角色
     *
     * @param identity
     * @return
     */
    RoleModel findByIdentity(String identity);

}
