package com.fast.dev.auth.center.server.core.service.impl;

import com.fast.dev.auth.center.server.core.annotations.CleanUserCache;
import com.fast.dev.auth.center.server.core.dao.RoleDao;
import com.fast.dev.auth.center.server.core.dao.UserDao;
import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.auth.center.server.core.domain.User;
import com.fast.dev.auth.center.server.core.helper.AuthEventOutputStreamHelper;
import com.fast.dev.auth.center.server.core.type.CleanUserCacheType;
import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.model.*;
import com.fast.dev.auth.client.service.UserService;
import com.fast.dev.auth.client.type.AuthEventAction;
import com.fast.dev.auth.client.type.AuthEventType;
import com.fast.dev.auth.client.type.ResultState;
import com.fast.dev.data.base.util.PageEntityUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.helper.ReIndexHelper;
import com.fast.dev.data.mongo.model.QueryModel;
import com.fast.dev.ucenter.core.model.BaseUserModel;
import com.fast.dev.ucenter.core.service.UserManagerService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    @Autowired
    private RoleDao roleDao;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private ReIndexHelper reIndexHelper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserManagerService userManagerService;


    @Autowired
    private AuthEventOutputStreamHelper authEventOutputStreamHelper;

    @Override
    @UserLog(value = ResourceAuthConstant.Manager, parameter = {"#enterpriseId", "#userModel"})
    @CleanUserCache(value = "#userModel.uid", type = CleanUserCacheType.User)
    public ResultContent updateUser(String enterpriseId, UserModel userModel) {
        Assert.hasText(enterpriseId, "企业ID不能为空");

        if (!StringUtils.hasText(userModel.getUid())) {
            return ResultContent.build(ResultState.UserNotExist);
        }
        String uid = this.userDao.updateUser(enterpriseId, userModel);
        if (StringUtils.hasText(uid)) {
            this.authEventOutputStreamHelper.publish(AuthEventType.User, AuthEventAction.Update, new HashMap<>() {{
                put("enterpriseId", enterpriseId);
                put("uid", uid);
            }});
        }
        return ResultContent.buildContent(uid);
    }

    @Override
    public ResultContent<UserModel> getUser(String enterpriseId, String uid) {
        return ResultContent.buildContent(userToModel(this.userDao.findAndSaveUser(enterpriseId, uid)));
    }

    @Override
    public ResultContent<Page<EnterpriseModel>> getEnterprise(String uid, Pageable pageable) {
        return ResultContent.buildContent(
                this.userDao.getEnterprise(uid, pageable).stream().map((it) -> {
                    return enterpriseToModel(it);
                }).collect(Collectors.toList()));
    }

    @Override
    public ResultContent<List<EnterpriseModel>> getAffiliatedEnterprises(String uid) {
        List<Enterprise> enterprises = this.userDao.getAffiliatedEnterprises(uid);
        if (enterprises == null) {
            return ResultContent.build(ResultState.Fail);
        }
        return ResultContent.buildContent(enterprises.stream().map((it) -> {
            return enterpriseToModel(it);
        }).collect(Collectors.toList()));
    }

    @Override
    public Page<EnterpriseUserModel> queryEnterpriseUser(String mql, Pageable pageable) {
        return PageEntityUtil.concurrent2PageModel(this.userDao.queryEnterpriseUser(mql, pageable), (user) -> {
            return userToEnterpriseUserModel(user);
        });
    }

    @Override
    public Page<EnterpriseUserModel> queryUser(UserQueryModel userQueryModel, Pageable pageable) {
        QueryModel queryModel = new QueryModel();
        BeanUtils.copyProperties(userQueryModel, queryModel);
        return PageEntityUtil.concurrent2PageModel(this.dbHelper.queryByMql(queryModel, pageable, User.class), (user) -> {
            return userToEnterpriseUserModel(user);
        });
    }

    @Override
    public ResultContent<Set<String>> getUserInfoKey() {
        return ResultContent.buildContent(this.reIndexHelper.getIndexNamesFromMap(User.class, "info"));
    }


    /**
     * @param enterprise
     * @return
     */
    public EnterpriseModel enterpriseToModel(Enterprise enterprise) {
        EnterpriseModel enterpriseModel = new EnterpriseModel();
        BeanUtils.copyProperties(enterprise, enterpriseModel);
        return enterpriseModel;
    }


    /**
     * 用户实体转换为用户模型
     *
     * @param user
     * @return
     */
    @SneakyThrows
    public UserModel userToModel(User user) {
        if (user == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(user, userModel);
        return userModel;
    }


    /**
     * 转企业用户模型
     *
     * @param user
     * @return
     */
    public EnterpriseUserModel userToEnterpriseUserModel(User user) {
        EnterpriseUserModel enterpriseUserModel = new EnterpriseUserModel();
        BeanUtils.copyProperties(user, enterpriseUserModel, "enterprise", "roles", "familyModel");
        Enterprise enterprise = user.getEnterprise();
        if (enterprise != null) {
            enterpriseUserModel.setEnterpriseId(enterprise.getId());
        }

        //查询角色
        Optional.ofNullable(user.getRoles()).ifPresent((roleId) -> {
            enterpriseUserModel.setRoles(roleDao.findByIdIn(new ArrayList<String>(roleId)).parallelStream().map((role) -> {
                return RoleServiceImpl.roleToModel(role);
            }).collect(Collectors.toSet()));
        });

        //家庭组
        Optional.ofNullable(user.getFamily()).ifPresent((it) -> {
            UserQueryFamilyModel familyModel = new UserQueryFamilyModel();
            //转换为家庭组
            familyModel.setMember(it.getMember().stream().map((memberModel) -> {
                UserQueryFamilyMember familyMember = new UserQueryFamilyMember();
                BeanUtils.copyProperties(memberModel, familyMember);
                return familyMember;
            }).collect(Collectors.toSet()));
            enterpriseUserModel.setFamily(familyModel);
        });


        return enterpriseUserModel;
    }


    /**
     * 更新登录名
     */
    @UserLog
    public void updateLoginName(String uid) {
        BaseUserModel baseUserModel = this.userManagerService.queryUserId(uid);
        if (baseUserModel == null || !StringUtils.hasText(baseUserModel.getId())) {
            return;
        }
        //手机号码
        final String phone = baseUserModel.getPhone();
        log.info("uid :  {} -> {}", uid, phone);

        //更新冗余到User表的缓存,原子性操作
        this.userDao.updateUserPhone(uid, phone);

    }


}
