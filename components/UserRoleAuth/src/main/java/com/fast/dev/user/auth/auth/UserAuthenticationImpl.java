package com.fast.dev.user.auth.auth;


import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.security.model.UserIdentity;
import com.fast.dev.ucenter.security.service.UserAuthentication;
import com.fast.dev.user.auth.conf.UserRoleAuthConf;
import com.fast.dev.user.auth.dao.RoleDao;
import com.fast.dev.user.auth.domain.Role;
import com.fast.dev.user.auth.domain.User;
import com.fast.dev.user.auth.domain.UserRole;
import com.fast.dev.user.auth.model.DetailsRoleModel;
import com.fast.dev.user.auth.service.RoleService;
import com.fast.dev.user.auth.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class UserAuthenticationImpl implements UserAuthentication {


    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserRoleAuthConf userRoleAuthConf;


    @Autowired
    private RoleService roleService;


    @Autowired
    private UserService userService;


    @Override
    public UserIdentity authentication(UserTokenModel userTokenModel) {

        User user = getUser(userTokenModel.getUid());

        //是否已禁用
        if (user.isDisable()) {
            return null;
        }


        //权限列表
        Set<String> auths = new HashSet<String>() {{
            //全局默认的权限控制
            add(userRoleAuthConf.getDefaultLoginAuth());
        }};


        //身份列表
        Set<String> identitys = new HashSet<String>();


        List<DetailsRoleModel> roleModelSet = new ArrayList<>();
        //用户角色
        List<UserRole> userRoles = roleDao.listUserRole(user.getId());
        //获取所有的角色及权限
        if (userRoles != null) {
            userRoles.forEach((it) -> {

                Role role = it.getRole();
                if (role == null || StringUtils.isEmpty(role.getRoleName())) {
                    return;
                }

                //获取该角色的所有权限关系的详情
                DetailsRoleModel detailsRoleModel = roleService.detailsRole(role.getRoleName());
                if (detailsRoleModel != null) {
                    //继承的权限与自身的权限
                    addRoleAuth(auths, detailsRoleModel.getExtendAuth());
                    addRoleAuth(auths, detailsRoleModel.getAuth());

                    //角色集合
                    roleModelSet.add(detailsRoleModel);

                    //身份集合
                    identitys.add(detailsRoleModel.getIdentity());

                }

            });

        }

        //去除空字符串与null
        cleanNull(identitys, auths);

        //取出身份与权限的的hash
        String authHash = authIdentitysHash(identitys, auths);

        //详情
        Map<String, Object> other = new HashMap<>();
        other.put("time", System.currentTimeMillis());
        other.put("role", roleModelSet);
        other.put("user", user);
        other.put("identity", identitys);
        other.put("authHash", authHash);

        //身份
        UserIdentity userIdentity = new UserIdentity(auths, other);

        return userIdentity;
    }


    /**
     * 清空null与空字符串
     *
     * @param values
     */
    private void cleanNull(Set<String>... values) {
        for (Set<String> val : values) {
            val.remove("");
            val.remove(null);
        }
    }


    @SneakyThrows
    private String authIdentitysHash(Set<String> identitys, Set<String> auths) {

        List<String> identityList = new ArrayList<>(identitys);
        Collections.sort(identityList);

        List<String> authList = new ArrayList<>(auths);
        Collections.sort(authList);

        StringBuffer sb = new StringBuffer();
        identityList.forEach((it) -> {
            sb.append("i:" + it + ",");
        });
        authList.forEach((it) -> {
            sb.append("a:" + it + ",");
        });
        return DigestUtils.md5DigestAsHex(sb.toString().getBytes("UTF-8"));
    }


    /**
     * 添加角色的权限
     */
    private static void addRoleAuth(Set<String> allAuth, Set<String> auth) {
        if (auth != null) {
            allAuth.addAll(auth);
        }
    }

    private User getUser(String uid) {
        return this.userService.findAndSaveUser(uid);
    }


}
