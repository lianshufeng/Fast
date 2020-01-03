package com.fast.dev.user.auth.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.fast.dev.user.auth.conf.UserRoleAuthConf;
import com.fast.dev.user.auth.dao.RoleDao;
import com.fast.dev.user.auth.dao.extend.RoleDaoExtend;
import com.fast.dev.user.auth.domain.Role;
import com.fast.dev.user.auth.domain.UserRole;
import com.fast.dev.user.auth.model.RoleModel;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.List;

@Log
public class RoleDaoImpl implements RoleDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private RoleDao me;

    @Autowired
    private UserRoleAuthConf userRoleAuthConf;


    @Autowired
    private RoleDao roleDao;


    @Override
    public long removeRole(String roleName) {

        Role role = this.mongoTemplate.findOne(new Query(Criteria.where("roleName").is(roleName)), Role.class);
        if (role == null) {
            return 0;
        }


        //删除这个角色
        this.mongoTemplate.remove(role);
        //删除用户的关联表
        this.mongoTemplate.remove(new Query(Criteria.where("role").is(role)), UserRole.class);
        return 1;
    }

    @Override
    public boolean updateRole(String roleName, String remark) {

        Query query = new Query();
        query.addCriteria(Criteria.where("roleName").is(roleName));

        Update update = new Update();
        update.setOnInsert("roleName", roleName);
        update.set("remark", remark);
        dbHelper.saveTime(update);

        this.mongoTemplate.upsert(query, update, Role.class);

        return true;
    }

    @Override
    public boolean updateRole(RoleModel roleModel) {

        //查询对应的角色
        Query query = Query.query(Criteria.where("roleName").is(roleModel.getRoleName()));

        //更新条件
        Update update = new Update();
        EntityObjectUtil.entity2Update(roleModel, update, "parent");


        //父类
        if (StringUtils.hasText(roleModel.getParent())) {
            //检查是否存在嵌套的父类关系
            if (canSetParent(roleModel.getRoleName(), roleModel.getParent())) {
                Role parent = this.me.findByRoleName(roleModel.getParent());
                if (parent != null) {
                    update.set("parent", parent);
                }
            }
        } else if ("".equals(roleModel.getParent())) {
            update.unset("parent");
        }

        //更新时间
        this.dbHelper.saveTime(update);

        UpdateResult updateResult = this.mongoTemplate.upsert(query, update, Role.class);

        return updateResult.getModifiedCount() > 0 || updateResult.getUpsertedId() != null;
    }

    @Override
    public boolean setRoleAuth(String roleName, String... auth) {

        Query query = new Query();
        query.addCriteria(Criteria.where("roleName").is(roleName));

        Update update = new Update();
        update.set("auth", auth);
        dbHelper.updateTime(update);

        return this.mongoTemplate.updateFirst(query, update, Role.class).getModifiedCount() > 0;
    }

    @Override
    public long addUserRole(String roleName, String... userId) {
        BulkWriteResult bulkWriteResult = executeUpdateUserRoleBulkOperations(roleName, userId, new UpdateUserRoleBulkOperations() {
            @Override
            public void execute(BulkOperations bulkOperations, Role role, Object user) {
                Query query = new Query();
                query.addCriteria(Criteria.where("user").is(user).and("role").is(role));

                Update update = new Update();
                update.set("user", user);
                update.set("role", role);
                dbHelper.saveTime(update);

                bulkOperations.upsert(query, update);
            }
        });

        return bulkWriteResult != null ? bulkWriteResult.getUpserts().size() : 0;
    }

    @Override
    public long removeUserRole(String roleName, String... userId) {
        BulkWriteResult bulkWriteResult = executeUpdateUserRoleBulkOperations(roleName, userId, new UpdateUserRoleBulkOperations() {
            @Override
            public void execute(BulkOperations bulkOperations, Role role, Object user) {
                Query query = new Query();
                query.addCriteria(Criteria.where("user").is(user).and("role").is(role));
                bulkOperations.remove(query);
            }
        });
        return bulkWriteResult != null ? bulkWriteResult.getDeletedCount() : 0;
    }

    @Override
    public Page<UserRole> listRoleUser(String roleName, Pageable pageable) {
        Role role = this.mongoTemplate.findOne(new Query(Criteria.where("roleName").is(roleName)), Role.class);
        if (role == null) {
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("role").is(role));
        return this.dbHelper.pages(query, pageable, UserRole.class);
    }

    @Override
    public Page<UserRole> listUserRole(String userId, Pageable pageable) {
        Object user = this.mongoTemplate.findById(userId, userRoleAuthConf.getUserEntityCls());
        if (user == null) {
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("user").is(user));
        return this.dbHelper.pages(query, pageable, UserRole.class);
    }

    @Override
    public List<UserRole> listUserRole(String userId) {
        Object user = this.mongoTemplate.findById(userId, userRoleAuthConf.getUserEntityCls());
        if (user == null) {
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("user").is(user));
        return this.mongoTemplate.find(query, UserRole.class);
    }

    @Override
    public boolean setRoleParent(String roleName, String parentRoleName) {
        if (!canSetParent(roleName, parentRoleName)) {
            log.info("角色得父类不得有嵌套关系");
            return false;
        }


        //准备继承的角色
        Role parentRole = this.me.findByRoleName(parentRoleName);
        if (parentRole == null) {
            return false;
        }


        Update update = new Update();
        update.set("parent", parentRole);
        this.dbHelper.updateTime(update);


        return this.mongoTemplate.updateFirst(new Query(Criteria.where("roleName").is(roleName)), update, Role.class).getModifiedCount() > 0;
    }

    @Override
    public boolean removeRoleParent(String roleName) {
        Update update = new Update();
        update.unset("parent");
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(new Query(Criteria.where("roleName").is(roleName)), update, Role.class).getModifiedCount() > 0;
    }

    @Override
    public boolean setRoleIdentity(String roleName, String identity) {
        Update update = new Update();
        update.set("identity", identity);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(new Query(Criteria.where("roleName").is(roleName)), update, Role.class).getModifiedCount() > 0;
    }

    /**
     * 执行更新用户角色,支持批量操作
     *
     * @param roleName
     * @param userId
     * @param updateUserRoleBulkOperations
     * @return
     */
    private BulkWriteResult executeUpdateUserRoleBulkOperations(String roleName, String[] userId, UpdateUserRoleBulkOperations updateUserRoleBulkOperations) {

        //角色中的空用户id
        if (userId == null || userId.length == 0) {
            return null;
        }

        //角色不存在
        Role role = this.mongoTemplate.findOne(new Query(Criteria.where("roleName").is(roleName)), Role.class);
        if (role == null) {
            return null;
        }

        int setup = 0;
        BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, UserRole.class);
        for (String u : userId) {
            Object user = this.mongoTemplate.findById(u, userRoleAuthConf.getUserEntityCls());
            if (user == null) {
                continue;
            }

            //进行迭代处理
            updateUserRoleBulkOperations.execute(bulkOperations, role, user);


            setup++;
        }
        //如果没有加入批量修改的数据则返回0
        if (setup == 0) {
            return null;
        }


        return bulkOperations.execute();
    }

    /**
     * 是否可以设置父类
     *
     * @param roleName
     * @param parentRoleName
     * @return
     */
    private boolean canSetParent(String roleName, String parentRoleName) {
        if (roleName.equals(parentRoleName)) {
            return false;
        }
        //不允许环形继承
        Role parent = this.roleDao.findByRoleName(parentRoleName);
        while (parent != null) {
            if (roleName.equals(parent.getRoleName())) {
                return false;
            }
            parent = parent.getParent();
        }
        return true;
    }


    /**
     * 更新用户角色的接口
     */
    interface UpdateUserRoleBulkOperations {
        void execute(BulkOperations bulkOperations, Role role, Object user);
    }
}
