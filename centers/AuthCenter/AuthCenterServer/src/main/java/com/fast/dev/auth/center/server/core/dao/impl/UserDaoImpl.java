package com.fast.dev.auth.center.server.core.dao.impl;

import com.fast.dev.auth.center.server.core.dao.EnterpriseDao;
import com.fast.dev.auth.center.server.core.dao.RoleDao;
import com.fast.dev.auth.center.server.core.dao.UserDao;
import com.fast.dev.auth.center.server.core.dao.extend.UserDaoExtend;
import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.auth.center.server.core.domain.User;
import com.fast.dev.auth.center.server.core.model.FamilyModel;
import com.fast.dev.auth.client.model.UserInfoModel;
import com.fast.dev.auth.client.model.UserModel;
import com.fast.dev.core.util.encode.HashUtil;
import com.fast.dev.data.base.util.PageEntityUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.helper.ReIndexHelper;
import com.fast.dev.data.mongo.model.QueryModel;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.fast.dev.ucenter.core.model.BaseUserModel;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class UserDaoImpl implements UserDaoExtend {

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private ReIndexHelper reIndexHelper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EnterpriseDao enterpriseDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RemoteUserCenterService remoteUserCenterService;


    @Override
    public User findAndSaveUser(String enterpriseId, String uid) {
        return updateAndFindUser(enterpriseId, uid);
    }

    @Override
    public String updateUser(String enterpriseId, UserModel userModel) {
        if (userModel.getInfo() == null) {
            return null;
        }

        //获取需要修改的用户
        User user = getUserKeepInfo(enterpriseId, userModel.getUid());
        if (user == null) {
            log.info("用户不存在 : " + enterpriseId + " -> " + userModel.getUid());
            return null;
        }
        //仅做增量修改
        for (Map.Entry<String, String> it : userModel.getInfo().entrySet()) {
            user.getInfo().put(it.getKey(), it.getValue());
        }

        this.dbHelper.updateTime(user);
        this.userDao.save(user);
        return user.getId();
    }

    @Override
    public Page<Enterprise> getEnterprise(String uid, Pageable pageable) {
        Query query = new Query(Criteria.where("uid").is(uid));
        return PageEntityUtil.concurrent2PageModel(this.dbHelper.pages(query, pageable, User.class), (user) -> {
            return ((User) user).getEnterprise();
        });
    }

    @Override
    public List<Enterprise> getAffiliatedEnterprises(String uid) {
        Query query = Query.query(Criteria.where("uid").is(uid).andOperator(
                Criteria.where("roles").exists(true),
                Criteria.where("$where").is("this.roles ? this.roles.length > 0 : false")
        ));
        List<User> users = this.mongoTemplate.find(query, User.class);
        if (users == null) {
            return null;
        }
        //过滤无效的企业
        return users.parallelStream().filter((user) -> {
            Enterprise enterprise = user.getEnterprise();
            return enterprise != null && StringUtils.hasText(enterprise.getId());
        }).map((user) -> {
            return user.getEnterprise();
        }).collect(Collectors.toList());
    }

    @Override
    public Set<String> addRole(String uid, String... roleId) {
        if (uid == null || roleId == null) {
            return null;
        }

        Set<String> ret = new HashSet<>();
        Arrays.stream(roleId).map((id) -> {
            return this.roleDao.findTop1ById(id);
        }).forEach((role) -> {
            Query query = new Query(Criteria.where("uid").is(uid).and("enterprise").is(role.getEnterprise()));
            Update update = new Update();
            update.addToSet("roles", role.getId());
            this.dbHelper.updateTime(update);
            if (this.mongoTemplate.updateFirst(query, update, User.class).getModifiedCount() > 0) {
                ret.add(role.getId());
            }
        });
        return ret;
    }

    @Override
    public Set<String> removeRole(String uid, String... roleId) {
        if (uid == null || roleId == null) {
            return null;
        }

        Set<String> ret = new HashSet<>();
        Arrays.stream(roleId).map((id) -> {
            return this.roleDao.findTop1ById(id);
        }).forEach((role) -> {
            Query query = new Query(Criteria.where("uid").is(uid).and("enterprise").is(role.getEnterprise()));
            Update update = new Update();
            update.pull("roles", role.getId());
            this.dbHelper.updateTime(update);
            if (this.mongoTemplate.updateFirst(query, update, User.class).getModifiedCount() > 0) {
                ret.add(role.getId());
            }
        });
        return ret;
    }

    @Override
    public boolean removeUserInfo(String enterpriseId, String uid, String... key) {
        if (key == null) {
            return false;
        }
        User user = getUserKeepInfo(enterpriseId, uid);
        for (String s : key) {
            user.getInfo().remove(s);
        }
        this.dbHelper.updateTime(user);
        return StringUtils.hasText(this.mongoTemplate.save(user).getId());
    }

    @Override
    @SneakyThrows
    public Page<User> queryEnterpriseUser(String mql, Pageable pageable) {
        return this.dbHelper.queryByMql(QueryModel.builder().mql(mql).build(), pageable, User.class);
    }

    @Override
    public void setUserFamily(FamilyModel familyModel, String epId, String... uid) {

        //确保一定有用户
        for (String u : uid) {
            findAndSaveUser(epId, u);
        }


        Query query = new Query();
        Criteria criteria = new Criteria();

        if (uid == null || uid.length == 0) {
            criteria.andOperator(
                    new Criteria("enterprise").is(Enterprise.build(epId))
            );
        } else {
            criteria.andOperator(
                    new Criteria("enterprise").is(Enterprise.build(epId)),
                    EntityObjectUtil.createQueryBatch("uid", uid)
            );
        }

        query.addCriteria(criteria);

        Update update = new Update();
        if (familyModel == null) {
            update.unset("family");
        } else {
            update.set("family", familyModel);
        }
        this.dbHelper.updateTime(update);

        this.mongoTemplate.updateMulti(query, update, User.class);

    }


    @Override
    public void reIndexInfo() {
        log.info("重建索引 : " + UserDaoImpl.class.getSimpleName());
        reIndexHelper.reIndexFromMap(User.class, "info");
    }

    @Override
    public void updateUserPhone(final String uid, final String phone) {
        log.info("[ updateUserPhone ] - {} - {}", uid, phone);
        //更新info.phone
        this.mongoTemplate.updateMulti(new Query(Criteria.where("uid").is(uid)), Update.update("info.phone", phone), User.class).getModifiedCount();

        //更新 family,查询到family在修改
        this.mongoTemplate.find(new Query(Criteria.where("family.member.uid").is(uid)), User.class).stream().forEach((user) -> {
            FamilyModel family = user.getFamily();
            //过滤仅uid相同的成员
            family.getMember().stream().filter((member) -> {
                return uid.equals(member.getUid());
            }).forEach((member) -> {
                member.setPhone(phone);
            });

            //同步到数据库,企业+uid
            this.mongoTemplate.updateMulti(new Query(Criteria.where("uid").is(user.getUid()).and("enterprise").is(user.getEnterprise())), Update.update("family", family), User.class);

        });

    }


    /**
     * 查询缓存企业信息
     *
     * @param cache
     * @return
     */
    private Enterprise queryCacheEnterprise(Map<String, Enterprise> cache, String enterpriseId) {
        Enterprise enterprise = cache.get(enterpriseId);
        if (enterprise == null) {
            enterprise = this.enterpriseDao.findTop1ById(enterpriseId);
            cache.put(enterpriseId, enterprise);
        }
        return enterprise;
    }

    private User updateAndFindUser(String enterpriseId, String uid) {
        Enterprise enterprise = enterpriseDao.findTop1ById(enterpriseId);
        //未找到企业
        if (enterprise == null) {
            return null;
        }
        //企业被禁用
        if (enterprise.getDisable() != null && enterprise.getDisable() == true) {
            log.info(String.format("enterprise [%s] is disable", enterpriseId));
            return null;
        }

        // 创建时间
        long createTime = this.dbHelper.getTime();
        Query query = new Query(Criteria.where("uid").is(uid).and("enterprise").is(enterprise));
        Update update = new Update();
        update.setOnInsert("uid", uid);
        update.setOnInsert("enterprise", enterprise);
        update.setOnInsert("uniqueIndex", HashUtil.hash(enterpriseId, uid));
        update.setOnInsert("createTime", createTime);

        //查询更改
        User user = this.mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true), User.class);

        //新建
        if (user.getCreateTime() != null && user.getCreateTime() == createTime) {
            user.setInfo(new HashMap<String, String>() {{
                BaseUserModel baseUserModel = remoteUserCenterService.queryUserId(user.getUid());
                if (baseUserModel != null && StringUtils.hasText(baseUserModel.getId())) {
                    put(UserInfoModel.Phone, baseUserModel.getPhone());
                }
            }});
            this.mongoTemplate.save(user);
        }

        return user;

    }


    /**
     * 查询并获取当前用户的info信息
     *
     * @param enterpriseId
     * @param uid
     * @return
     */
    private User getUserKeepInfo(String enterpriseId, String uid) {
        User user = userDao.findAndSaveUser(enterpriseId, uid);
        if (user == null) {
            return null;
        }
        if (user.getInfo() == null) {
            user.setInfo(new HashMap<>());
        }
        return user;
    }


}
