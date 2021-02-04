package com.fast.dev.auth.center.server.core.dao;

import com.fast.dev.auth.center.server.core.dao.extend.RoleDaoExtend;
import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.auth.center.server.core.domain.Role;
import com.fast.dev.data.mongo.dao.MongoDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleDao extends MongoDao<Role>, RoleDaoExtend {


    /**
     * 分页查询角色列表
     *
     * @param roleName
     * @param pageable
     * @return
     */
    Page<Role> findByEnterpriseAndRoleNameLike(Enterprise enterprise, String roleName, Pageable pageable);


    /**
     * 查询角色名称
     *
     * @return
     */
    Role findTop1ById(String roleId);


    /**
     * 唯一索引是否存在
     *
     * @param uniqueIndex
     * @return
     */
    boolean existsByUniqueIndex(String uniqueIndex);


    /**
     * 查询该企业下的所有角色
     *
     * @param enterprise
     * @return
     */
    List<Role> findByEnterprise(Enterprise enterprise);


    /**
     * 通过企业id与角色名查询企业下的某一个角色
     *
     * @param enterprise
     * @param roleName
     * @return
     */
    Role findByEnterpriseAndRoleName(Enterprise enterprise, String roleName);


    /**
     * 查询指定的角色
     *
     * @param roleIds
     * @return
     */
    List<Role> findByIdIn(List<String> roleIds);


}
