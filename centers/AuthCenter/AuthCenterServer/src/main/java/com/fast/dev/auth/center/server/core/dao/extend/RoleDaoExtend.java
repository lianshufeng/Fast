package com.fast.dev.auth.center.server.core.dao.extend;

import com.fast.dev.auth.center.server.core.domain.Role;
import com.fast.dev.auth.center.server.core.domain.UserRole;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.client.model.RoleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleDaoExtend {


    /**
     * 删除角色
     *
     * @return
     */
    long removeRole(String roleId);

    /**
     * 更新角色
     *
     * @return
     */
    /**
     * 更新角色
     *
     * @return
     */
    ResultContent<String> updateRole(RoleModel roleModel);


    /**
     * 增量更新
     *
     * @param roleModel
     * @return
     */
    Role findAndIncUserInfo(String enterpriseId, RoleModel roleModel);


    /**
     * 分页条件查询
     *
     * @param roleModel
     * @param pageable
     * @return
     */
    Page<Role> list(String enterpriseId, RoleModel roleModel, Pageable pageable);


    /**
     * 设置角色对应的权限
     *
     * @param auth
     * @return
     */
    boolean setRoleAuth(String roleId, String... auth);


    /**
     * 在角色中设置用户
     *
     * @param uid
     * @return
     */
    long addUserRole(String roleId, String... uid);


    /**
     * 删除角色中的用户
     *
     * @param uid
     * @return
     */
    long removeUserRole(String roleId, String... uid);

    /**
     * 查询角色中的用户
     *
     * @return
     */
    Page<UserRole> listRoleUser(String[] roleId, Pageable pageable);

    /**
     * 查询角色中的所有用户
     *
     * @param roleId
     * @return
     */
    List<UserRole> listRoleUser(String roleId);


    /**
     * 查询用户中有哪些角色
     *
     * @param uid
     * @param pageable
     * @return
     */
    Page<UserRole> listUserRole(String enterpriseId, String uid, Pageable pageable);


    /**
     * 查询该用户所有的角色
     *
     * @param uid
     * @return
     */
    List<UserRole> listUserRole(String enterpriseId, String uid);


    /**
     * 通过权限名查询所有的角色列表
     *
     * @param enterpriseId
     * @param authName
     * @return
     */
    List<Role> findByAuthName(String enterpriseId, String authName);


    /**
     * 通过身份明查询角色
     *
     * @param enterpriseId
     * @param identity
     * @return
     */
    List<Role> findByIdentity(String enterpriseId, String... identity);

}
