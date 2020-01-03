package com.fast.dev.user.auth.service.impl;

import com.fast.dev.data.base.util.PageEntityUtil;
import com.fast.dev.ucenter.security.helper.UserHelper;
import com.fast.dev.user.auth.conf.UserRoleAuthConf;
import com.fast.dev.user.auth.dao.RoleDao;
import com.fast.dev.user.auth.dao.UserRoleDao;
import com.fast.dev.user.auth.domain.Role;
import com.fast.dev.user.auth.domain.User;
import com.fast.dev.user.auth.domain.UserRole;
import com.fast.dev.user.auth.model.DetailsRoleModel;
import com.fast.dev.user.auth.model.RoleModel;
import com.fast.dev.user.auth.service.RoleService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Log
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private UserRoleAuthConf userRoleAuthConf;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserHelper userHelper;


    @Override
    @Transactional
    public boolean updateRole(RoleModel roleModel) {
        return this.roleDao.updateRole(roleModel);
    }

    @Override
    @Transactional
    public boolean setRoleParent(String roleName, String parentRoleName) {
        //不允许自己继承自己
        if (roleName.equals(parentRoleName)) {
            log.info("自己不允许继承自己");
            return false;
        }

        //角色非空判断
        Role role = this.roleDao.findByRoleName(roleName);
        if (role == null) {
            log.info("角色不存在");
            return false;
        }


        return this.roleDao.setRoleParent(roleName, parentRoleName);
    }

    @Override
    @Transactional
    public boolean removeRoleParent(String roleName) {
        return this.roleDao.removeRoleParent(roleName);
    }


    @Override
    @Transactional
    public boolean removeRole(String roleName) {

        //如果自身在这个角色中，就不能删除该角色
        User user = getCurrentUser();
        if (user != null && this.userRoleDao.existsByUserAndRole(user.getId(), roleName)) {
            log.info("自己在角色中,不允许删除自身");
            return false;
        }

        return this.roleDao.removeRole(roleName) > 0;
    }

    @Override
    public Page<RoleModel> listRole(String roleName, Pageable pageable) {
        if (roleName == null) {
            roleName = "";
        }
        return PageEntityUtil.toPageModel(this.roleDao.findByRoleNameLike(roleName, pageable), new PageEntityUtil.DataClean<Role, RoleModel>() {
            @Override
            public RoleModel execute(Role data) {
                return roleToModel(data);
            }
        });
    }

    @Override
    @Transactional
    public boolean setRoleAuth(String roleName, String... authName) {
        return this.roleDao.setRoleAuth(roleName, authName);
    }

    @Override
    public DetailsRoleModel detailsRole(String roleName) {
        Role rootRole = this.roleDao.findByRoleName(roleName);
        if (rootRole == null) {
            return null;
        }
        //用于记录每一级角色
        List<DetailsRoleModel> orderDetailsRoleModel = new ArrayList<>();

        DetailsRoleModel rootDetailsRoleModel = new DetailsRoleModel();
        detailsRoleToModel(rootRole, rootDetailsRoleModel, orderDetailsRoleModel);

        //累计的权限
        Set<String> extendAuth = new HashSet<>();
        //计算继承的权限
        for (DetailsRoleModel detailsRoleModel : orderDetailsRoleModel) {
            detailsRoleModel.setExtendAuth(new HashSet<>(extendAuth));
            if (detailsRoleModel.getAuth() != null) {
                extendAuth.addAll(detailsRoleModel.getAuth());
            }
        }
        return rootDetailsRoleModel;
    }


    @Override
    public Set<String> getRoleAuth(String roleName) {
        Role role = this.roleDao.findByRoleName(roleName);
        if (role == null || role.getAuth() == null) {
            return new HashSet<>();
        }
        return role.getAuth();
    }

    @Override
    @Transactional
    public long addUserRole(String roleName, String... userId) {
        return this.roleDao.addUserRole(roleName, userId);
    }

    @Override
    @Transactional
    public long removeUserRole(String roleName, String... userIds) {
        //非空判断
        if (userIds == null || userIds.length == 0) {
            return 0;
        }
        //不允许自己删自己
        User user = getCurrentUser();
        List<String> users = Arrays.asList(userIds);
        if (user != null && users.contains(user.getId())) {
            log.info("不允许移除当前用户所在的角色");
            users.remove(user.getId());
        }

        return this.roleDao.removeUserRole(roleName, userIds);
    }


    @Override
    public Page<Object> listRoleUser(String roleName, Pageable pageable) {
        Page<UserRole> userRoles = this.roleDao.listRoleUser(roleName, pageable);
        if (userRoles == null) {
            return PageEntityUtil.buildEmptyPage(pageable);
        }
        return PageEntityUtil.toPageModel(userRoles, new PageEntityUtil.DataClean<UserRole, Object>() {
            @Override
            public Object execute(UserRole it) {
                return userToModel(it.getUser());
            }
        });
    }

    @Override
    public Page<RoleModel> listUserRole(String userId, Pageable pageable) {
        Page<UserRole> userRoles = this.roleDao.listUserRole(userId, pageable);
        if (userRoles == null) {
            return PageEntityUtil.buildEmptyPage(pageable);
        }
        List<RoleModel> roleModels = userRoles.stream().map((it) -> {
            return roleToModel(it.getRole());
        }).collect(Collectors.toList());
        return new PageImpl<RoleModel>(roleModels, pageable, userRoles.getTotalElements());
    }

    @Override
    @Transactional
    public boolean setRoleIdentity(String roleName, String identity) {
        return this.roleDao.setRoleIdentity(roleName, identity);
    }

    @Override
    public RoleModel findByIdentity(String identity) {
        Role role = this.roleDao.findByIdentity(identity);
        if (role == null) {
            return null;
        }
        return roleToModel(role);
    }


    /**
     * 转换到详情模型
     *
     * @param role
     * @param detailsRoleModel
     */
    private void detailsRoleToModel(Role role, DetailsRoleModel detailsRoleModel, List<DetailsRoleModel> orderDetailsRoleModel) {
        //自身相同属性的拷贝
        BeanUtils.copyProperties(role, detailsRoleModel, "parent");

        //父类
        Role parent = role.getParent();
        if (parent != null) {
            DetailsRoleModel parentDetailsRoleModel = new DetailsRoleModel();
            detailsRoleToModel(parent, parentDetailsRoleModel, orderDetailsRoleModel);

            //设置父类
            detailsRoleModel.setParent(parent.getRoleName());
            detailsRoleModel.setParentRole(parentDetailsRoleModel);
        }

        //一次记录，顶级最后一次进入列表
        orderDetailsRoleModel.add(detailsRoleModel);

    }


    /**
     * role实体转model
     *
     * @param role
     * @return
     */
    public RoleModel roleToModel(Role role) {
        RoleModel roleModel = new RoleModel();
        BeanUtils.copyProperties(role, roleModel, "parent");
        //设置父类的角色名称
        if (role.getParent() != null) {
            roleModel.setParent(role.getParent().getRoleName());
        }
        return roleModel;
    }


    /**
     * 用户实体转模型
     *
     * @param u
     * @return
     */
    @SneakyThrows
    private Object userToModel(User u) {
        if (u == null) {
            return null;
        }
        User user = this.mongoTemplate.findById(u.getId(), this.userRoleAuthConf.getUserEntityCls());
        Object userModel = userRoleAuthConf.getUserModelCls().newInstance();
        BeanUtils.copyProperties(user, userModel);
        return userModel;
    }


    /**
     * 取出当前的用户
     *
     * @return
     */
    private User getCurrentUser() {
        if (this.userHelper.getUser() == null) {
            return null;
        }
        Object u = this.userHelper.getUser().getDetails().get("user");
        if (u != null && u instanceof User) {
            return (User) u;
        }
        return null;
    }

}
