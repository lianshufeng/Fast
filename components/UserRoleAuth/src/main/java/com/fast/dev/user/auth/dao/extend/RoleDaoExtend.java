package com.fast.dev.user.auth.dao.extend;

import com.fast.dev.user.auth.domain.UserRole;
import com.fast.dev.user.auth.model.RoleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleDaoExtend {


    /**
     * 删除角色
     *
     * @param roleName
     * @return
     */
    long removeRole(String roleName);

    /**
     * 更新角色
     *
     * @param roleName
     * @param remark
     * @return
     */
    boolean updateRole(String roleName, String remark);


    /**
     * 更新角色
     * @param roleModel
     * @return
     */
    boolean updateRole(RoleModel roleModel);


    /**
     * 设置角色对应的权限
     *
     * @param roleName
     * @param auth
     * @return
     */
    boolean setRoleAuth(String roleName, String... auth);


    /**
     * 在角色中设置用户
     *
     * @param roleName
     * @param userId
     * @return
     */
    long addUserRole(String roleName, String... userId);


    /**
     * 删除角色中的用户
     *
     * @param roleName
     * @param userId
     * @return
     */
    long removeUserRole(String roleName, String... userId);

    /**
     * 查询角色中的用户
     *
     * @param roleName
     * @return
     */
    Page<UserRole> listRoleUser(String roleName, Pageable pageable);


    /**
     * 查询用户中有哪些角色
     *
     * @param userId
     * @param pageable
     * @return
     */
    Page<UserRole> listUserRole(String userId, Pageable pageable);


    /**
     * 查询该用户所有的角色
     *
     * @param userId
     * @return
     */
    List<UserRole> listUserRole(String userId);


    /**
     * 设置角色的继承关系
     *
     * @param roleName
     * @param parentRoleName
     * @return
     */
    boolean setRoleParent(String roleName, String parentRoleName);


    /**
     * 删除节点的父类
     *
     * @param roleName
     * @return
     */
    boolean removeRoleParent(String roleName);


    /**
     * 设置角色上关联的身份
     *
     * @param roleName
     * @param identity
     * @return
     */
    boolean setRoleIdentity(String roleName, String identity);


}
