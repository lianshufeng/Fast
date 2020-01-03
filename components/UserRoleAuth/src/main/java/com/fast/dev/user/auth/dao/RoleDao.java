package com.fast.dev.user.auth.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.user.auth.dao.extend.RoleDaoExtend;
import com.fast.dev.user.auth.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleDao extends MongoDao<Role>, RoleDaoExtend {


    /**
     * 删除角色
     *
     * @param roleName
     * @return
     */
    long removeByRoleName(String roleName);


    /**
     * 分页查询角色列表
     *
     * @param roleName
     * @param pageable
     * @return
     */
    Page<Role> findByRoleNameLike(String roleName, Pageable pageable);


    /**
     * 查询角色名称
     *
     * @param roleName
     * @return
     */
    Role findByRoleName(String roleName);


    /**
     * 通过身份查询角色
     *
     * @param identity
     * @return
     */
    Role findByIdentity(String identity);


}
