package com.fast.dev.auth.center.server.core.service.impl;

import com.fast.dev.auth.center.server.core.conf.DefaultRoleConf;
import com.fast.dev.auth.center.server.core.dao.EnterpriseDao;
import com.fast.dev.auth.center.server.core.dao.RoleDao;
import com.fast.dev.auth.center.server.core.dao.UserAuthDao;
import com.fast.dev.auth.center.server.core.dao.UserDao;
import com.fast.dev.auth.center.server.core.domain.Role;
import com.fast.dev.auth.center.server.core.domain.User;
import com.fast.dev.auth.client.bean.AuthUser;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.client.service.AuthService;
import com.fast.dev.auth.client.type.ResultState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private DefaultRoleConf defaultRoleConf;

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private EnterpriseDao enterpriseDao;


    @Override
    public ResultContent<Set<String>> queryUserAuth(String uid) {
        //默认的用户权限
        Set<String> autsh = new HashSet<String>() {{
            add(defaultRoleConf.getDefaultLoginAuth());
        }};

        //用户自身的权限
        autsh.addAll(this.userAuthDao.findUserAuth(uid));
        return ResultContent.buildContent(autsh);
    }

    @Override
    public ResultContent<AuthUser> queryUserEnterPrise(String uid, String epId) {
        User user = this.userDao.findAndSaveUser(epId, uid);
        if (user == null) {
            return ResultContent.build(ResultState.EnterpriseNotExist);
        }
        // 查询企业下这个用户有多少角色
        Set<Role> roles = queryUserRole(epId, uid);
        if (roles == null) {
            return null;
        }

        //汇总角色的权限与身份
        Set<String> authSet = new HashSet<>();
        Set<String> identitySet = new HashSet<>();
        summaryAuth(roles, authSet, identitySet);


        AuthUser authUser = new AuthUser();
        authUser.setAuth(authSet);
        authUser.setIdentity(identitySet);
        authUser.setRoles(roles.stream().map((it) -> {
            return it.getId();
        }).collect(Collectors.toSet()));

        //用户基本信息
        authUser.setUser(this.userService.userToModel(user));


        return ResultContent.buildContent(authUser);
    }


    /**
     * 查询企业下一个用户的所有角色
     *
     * @param epId
     * @param uid
     * @return
     */
    private Set<Role> queryUserRole(String epId, String uid) {
        return this.roleDao.listUserRole(epId, uid).stream().map((it) -> {
            return it.getRole();
        }).collect(Collectors.toSet());
    }

    /**
     * 汇总权限
     *
     * @param roles
     * @param auth
     */
    public static void summaryAuth(Set<Role> roles, Set<String> auth, Set<String> identity) {
        //多个角色
        roles.forEach((it) -> {
            summaryAuth(it, auth, identity);
        });
    }

    /**
     * 权限汇总
     *
     */
    private static void summaryAuth(Role role, Set<String> authSet, Set<String> identitySet) {
        //权限
        if (role.getAuth() != null) {
            authSet.addAll(role.getAuth().stream().filter((it) -> {
                return StringUtils.hasText(it);
            }).collect(Collectors.toSet()));
        }

        //身份
        if (role.getIdentity() != null) {
            identitySet.addAll(role.getIdentity().stream().filter((it) -> {
                return StringUtils.hasText(it);
            }).collect(Collectors.toSet()));
        }

    }


}
