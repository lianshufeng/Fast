package com.fast.dev.auth.center.server.core.service.impl;

import com.fast.dev.auth.center.server.core.annotations.AuthEvent;
import com.fast.dev.auth.center.server.core.annotations.CleanUserCache;
import com.fast.dev.auth.center.server.core.conf.DefaultRoleConf;
import com.fast.dev.auth.center.server.core.dao.RoleDao;
import com.fast.dev.auth.center.server.core.dao.UserDao;
import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.auth.center.server.core.domain.Role;
import com.fast.dev.auth.center.server.core.domain.UserRole;
import com.fast.dev.auth.center.server.core.helper.AuthEventOutputStreamHelper;
import com.fast.dev.auth.center.server.core.type.CleanUserCacheType;
import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.model.*;
import com.fast.dev.auth.client.service.RoleService;
import com.fast.dev.auth.client.type.AuthEventAction;
import com.fast.dev.auth.client.type.AuthEventType;
import com.fast.dev.auth.client.type.ResultState;
import com.fast.dev.auth.security.AuthClientUserHelper;
import com.fast.dev.auth.security.model.UserParmModel;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.data.base.util.PageEntityUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.model.QueryModel;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Log
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private DefaultRoleConf defaultRoleConf;

    @Autowired
    private AuthClientUserHelper userHelper;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private RoleServiceImpl me;


    @Autowired
    private AuthEventOutputStreamHelper authEventOutputStreamHelper;


    @Override
    @UserLog(value = ResourceAuthConstant.Manager, parameter = {"#model"})
    @CleanUserCache(value = "T(com.fast.dev.auth.center.server.core.util.EnterpriseRoleUserUtil).getUserId(#model)", type = CleanUserCacheType.User)
    public ResultContent updateEnterpriseRoleUser(EnterpriseRoleUserModel model) {
        log.info("updateEnterpriseRoleUser : " + JsonUtil.toJson(model));
        if (model == null) {
            return ResultContent.build(ResultState.ParmNotExist);
        }

        if (model.getRoleUserModels() == null) {
            return ResultContent.build(ResultState.RoleNotExist);
        }

        //过滤黑名单的角色名
        for (RoleUserModel roleUserModel : model.getRoleUserModels()) {
            if (checkAuthNameNotAllow(roleUserModel.getAuth())) {
                return ResultContent.build(ResultState.AuthNameNotAllow);
            }

            if (!StringUtils.hasText(roleUserModel.getRoleName())) {
                return ResultContent.build(ResultState.RoleNameNotNull);
            }
        }

        //企业id
        final String enterpriseId = model.getEnterpriseId();


        //记录角色与用户的关系
        final Map<String, Set<String>> roleUsers = new HashMap<>();


        //角色用户
        for (final RoleUserModel roleModel : model.getRoleUserModels()) {
            //增量更新角色信息
            Role role = this.roleDao.findAndIncUserInfo(enterpriseId, roleModel);
            if (role == null) {
                //回滚事务
                return ResultContent.build(ResultState.RoleNotExist);
            }

            //非空用户
            if (roleModel.getUserModel() == null) {
                continue;
            }


            //取出用户id
            final Set<String> uids = new HashSet<>();
            roleModel.getUserModel().forEach((userModel) -> {
                uids.add(userModel.getUid());
                //用户修改信息
                this.userDao.updateUser(enterpriseId, userModel);
            });

            //角色中添加用户
            this.roleDao.addUserRole(role.getId(), uids.toArray(new String[0]));
            //用户中添加角色
            uids.forEach((uid) -> {
                this.userDao.addRole(uid, role.getId());
            });

            roleUsers.put(role.getId(), uids);
        }

        //发布角色中添加用户的事件
        for (Map.Entry<String, Set<String>> entry : roleUsers.entrySet()) {
            this.publishRoleUserEvent(AuthEventAction.Update, entry.getKey(), entry.getValue().toArray(new String[0]));
        }

        return ResultContent.build(ResultState.Success);
    }

    @Override
    @Transactional
    @UserLog(value = ResourceAuthConstant.Manager, parameter = {"#roleModel"})
    @CleanUserCache(value = "#roleModel.id", type = CleanUserCacheType.Role)
    @AuthEvent(filter = "#ret.state.toString() == 'Success'", parm = {"#ret.content", "#roleModel"}, type = AuthEventType.Role, action = AuthEventAction.Update)
    public ResultContent updateRole(RoleModel roleModel) {
        //过滤黑名单的角色名
        if (checkAuthNameNotAllow(roleModel.getAuth())) {
            return ResultContent.build(ResultState.AuthNameNotAllow);
        }
        return this.roleDao.updateRole(roleModel);
    }


    /**
     * 检查是否包含黑名单不允许的权限名
     *
     * @param auth
     * @return
     */
    private boolean checkAuthNameNotAllow(Set<String> auth) {
        return auth != null && auth.size() > 0 && auth.containsAll(defaultRoleConf.getAuthNameBlacklist());
    }


    /**
     * 获取角色
     *
     * @param roleId
     * @return
     */
    @Override
    public ResultContent get(String roleId) {
        return ResultContent.buildContent(roleToModel(this.roleDao.findTop1ById(roleId)));
    }


    /**
     * 查询角色
     *
     * @param roleModel
     * @param pageable
     * @return
     */
    @Override
    public Page<RoleModel> list(RoleModel roleModel, Pageable pageable) {
        return PageEntityUtil.concurrent2PageModel(this.roleDao.list(roleModel.getEnterpriseId(), roleModel, pageable), (it) -> {
            return roleToModel(it);
        });
    }

    @Override
    public List<RoleModel> listRoles(String enterpriseId) {
        Enterprise enterprise = new Enterprise();
        enterprise.setId(enterpriseId);
        List<Role> roles = this.roleDao.findByEnterprise(enterprise);
        if (roles == null) {
            return null;
        }
        return roles.stream().map((it) -> {
            return roleToModel(it);
        }).collect(Collectors.toList());
    }

    @Override
    public RoleModel findByRoleName(String enterpriseId, String roleName) {
        return roleToModel(this.roleDao.findByEnterpriseAndRoleName(Enterprise.build(enterpriseId), roleName));
    }

    @Override
    public List<RoleModel> findByAuthName(String enterpriseId, String authName) {
        List<Role> roles = this.roleDao.findByAuthName(enterpriseId, authName);
        if (roles == null) {
            return null;
        }
        return roles.parallelStream().map((role) -> {
            return roleToModel(role);
        }).collect(Collectors.toList());
    }

    @Override
    public List<RoleModel> findByIdentity(String enterpriseId, String... identity) {
        List<Role> roles = this.roleDao.findByIdentity(enterpriseId, identity);
        if (roles == null) {
            return null;
        }
        return roles.parallelStream().map((role) -> {
            return roleToModel(role);
        }).collect(Collectors.toList());
    }


    @Transactional
    @UserLog(value = ResourceAuthConstant.Manager, parameter = {"#roleId"})
    @CleanUserCache(value = "#roleId", type = CleanUserCacheType.Role)
    @AuthEvent(filter = "#ret.state.toString() == 'Success'", parm = "#roleId", type = AuthEventType.Role, action = AuthEventAction.Remove)
    public ResultContent removeRole(String roleId) {
        Role role = this.roleDao.findTop1ById(roleId);
        if (role == null) {
            return ResultContent.build(ResultState.RoleNotExist);
        }
        //如果自身在这个角色中，就不能删除该角色
        UserParmModel user = getCurrentUser();
        if (user != null && user.getRoles().contains(roleId)) {
            return ResultContent.build(ResultState.UserInRole);
        }


        //删除用户表里的角色
        Optional.ofNullable(this.roleDao.listRoleUser(role.getId())).ifPresent((userRoles) -> {
            userRoles.forEach((userRole) -> {
                this.userDao.removeRole(userRole.getUser().getUid(), roleId);
            });
        });

        return ResultContent.build(this.roleDao.removeRole(roleId) > 0);
    }


    @Transactional
    @UserLog(value = ResourceAuthConstant.Manager, parameter = {"#roleId", "#uid"})
    @CleanUserCache(value = "#uid", type = CleanUserCacheType.User)
    public ResultContent addUserRole(String roleId, String... uid) {
        //角色组中添加用户
        if (this.roleDao.addUserRole(roleId, uid) > 0) {
            //用户添加角色
            for (String u : uid) {
                this.userDao.addRole(u, roleId);
            }

            //发布事件
            this.publishRoleUserEvent(AuthEventAction.Update, roleId, uid);

            return ResultContent.build(ResultState.Success, uid);
        }

        return ResultContent.build(false);
    }


    @Transactional
    @UserLog(value = ResourceAuthConstant.Manager, parameter = {"#roleId", "#uid"})
    @CleanUserCache(value = "#uid", type = CleanUserCacheType.User)
    public ResultContent removeUserRole(String roleId, String... uid) {
        //非空判断
        if (uid == null || uid.length == 0) {
            return ResultContent.build(ResultState.Fail);
        }
        //不允许自己删自己
        UserParmModel user = getCurrentUser();
        if (user != null) {
            uid = Arrays.stream(uid).filter((it) -> {
                return !it.equals(user.getUid());
            }).collect(Collectors.toSet()).toArray(new String[0]);
        }

        //删除角色
        if (this.roleDao.removeUserRole(roleId, uid) > 0) {
            for (String u : uid) {
                this.userDao.removeRole(u, roleId);
            }

            //发布事件
            this.publishRoleUserEvent(AuthEventAction.Remove, roleId, uid);

            return ResultContent.build(true);
        }

        return ResultContent.build(false);
    }


    /**
     * 查询角色中的所有用户
     *
     * @param roleId
     * @param pageable
     * @return
     */
    public Page<UserModel> listRoleUser(String[] roleId, Pageable pageable) {
        Page<UserRole> userRoles = this.roleDao.listRoleUser(roleId, pageable);
        if (userRoles == null) {
            return PageEntityUtil.buildEmptyPage(pageable);
        }

        return PageEntityUtil.concurrent2PageModel(userRoles, (UserRole it) -> {
            return userService.userToModel(it.getUser());
        });
    }

    /**
     * MQL分页条件查询角色列表
     *
     * @param userQueryModel
     * @param pageable
     * @return
     */
    @Override
    public Page<RoleModel> queryRole(UserQueryModel userQueryModel, Pageable pageable) {
        QueryModel queryModel = new QueryModel();
        BeanUtils.copyProperties(userQueryModel, queryModel);
        return PageEntityUtil.concurrent2PageModel(this.dbHelper.queryByMql(queryModel, pageable, Role.class), (role) -> {
            return roleToModel(role);
        });
    }


    /**
     * 获取用户有多少个角色信息
     *
     * @param userId
     * @param pageable
     * @return
     */
    public Page<RoleModel> listUserRole(String enterpriseId, String userId, Pageable pageable) {
        Page<UserRole> userRoles = this.roleDao.listUserRole(enterpriseId, userId, pageable);
        if (userRoles == null) {
            return PageEntityUtil.buildEmptyPage(pageable);
        }
        List<RoleModel> roleModels = userRoles.stream().map((it) -> {
            return roleToModel(it.getRole());
        }).collect(Collectors.toList());
        return new PageImpl<RoleModel>(roleModels, pageable, userRoles.getTotalElements());
    }


    /**
     * role实体转model
     *
     * @param role
     * @return
     */
    public static RoleModel roleToModel(Role role) {
        if (role == null) {
            return null;
        }
        RoleModel roleModel = new RoleModel();
        BeanUtils.copyProperties(role, roleModel, "enterprise");

        //设置企业
        Enterprise enterprise = role.getEnterprise();
        if (enterprise != null) {
            roleModel.setEnterpriseId(enterprise.getId());
        }

        return roleModel;
    }


    /**
     * 取出当前的用户
     *
     * @return
     */
    private UserParmModel getCurrentUser() {
        return this.userHelper.getUser();
    }


    /**
     * 发布角色中添加用户的事件
     */
    private void publishRoleUserEvent(AuthEventAction action, String roleId, String... uid) {
        this.authEventOutputStreamHelper.publish(AuthEventType.RoleUser, action, new HashMap<>() {{
            put("roleId", roleId);
            put("uid", uid);
        }});
    }

}
