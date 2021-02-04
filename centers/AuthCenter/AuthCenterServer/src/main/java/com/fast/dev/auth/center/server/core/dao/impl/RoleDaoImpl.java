package com.fast.dev.auth.center.server.core.dao.impl;

import com.fast.dev.auth.center.server.core.dao.EnterpriseDao;
import com.fast.dev.auth.center.server.core.dao.RoleDao;
import com.fast.dev.auth.center.server.core.dao.UserDao;
import com.fast.dev.auth.center.server.core.dao.extend.RoleDaoExtend;
import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.auth.center.server.core.domain.Role;
import com.fast.dev.auth.center.server.core.domain.User;
import com.fast.dev.auth.center.server.core.domain.UserRole;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.client.model.RoleModel;
import com.fast.dev.auth.client.type.ResultState;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.core.util.encode.HashUtil;
import com.fast.dev.data.base.util.PageEntityUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.mongodb.bulk.BulkWriteResult;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log
public class RoleDaoImpl implements RoleDaoExtend {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private RoleDao me;


    @Autowired
    private RoleDao roleDao;

    @Autowired
    private EnterpriseDao enterpriseDao;

    @Autowired
    private UserDao userDao;

    @Override
    public long removeRole(String roleId) {
        List<Role> roles = this.mongoTemplate.find(buildRoleQueryFromRoleId(roleId), Role.class);
        if (roles == null) {
            return 0;
        }
        //删除这个角色
        roles.forEach((role) -> {
            this.mongoTemplate.remove(role);
        });
        //删除用户的关联表
        this.mongoTemplate.remove(new Query(Criteria.where("role").in(roles)), UserRole.class);
        return 1;
    }

    @Override
    public ResultContent<String> updateRole(RoleModel roleModel) {

        Role role = this.roleDao.findTop1ById(roleModel.getId());
        if (role == null) {
            role = new Role();
        }


        //新增的时，角色名不能为空
        if (role.getId() == null && !StringUtils.hasText(roleModel.getRoleName())) {
            return ResultContent.build(ResultState.RoleNameNotNull);
        }


        //忽略要过滤的字段
        Set<String> ignoreProperties = new HashSet<>() {{
            add("id");
            add("enterpriseId");
        }};

        //过滤拷贝为null的字段
        BeanUtil.getNullPropertyNames(roleModel, ignoreProperties);

        //拷贝为数据库实体
        BeanUtils.copyProperties(roleModel, role, ignoreProperties.toArray(new String[0]));

        //如果新增，必须有企业依赖对象
        if (!StringUtils.hasText(role.getId())) {
            if (!StringUtils.hasText(roleModel.getEnterpriseId())) {
                return ResultContent.build(ResultState.EnterpriseNotExist);
            }
            Enterprise enterprise = this.enterpriseDao.findTop1ById(roleModel.getEnterpriseId());
            if (enterprise == null) {
                return ResultContent.build(ResultState.EnterpriseNotExist);
            }
            role.setEnterprise(enterprise);
        }

        //更新唯一索引
        String uniqueIndex = uniqueIndex(role.getEnterprise().getId(), role.getRoleName());
        if (!uniqueIndex.equals(role.getUniqueIndex()) && this.roleDao.existsByUniqueIndex(uniqueIndex)) {
            return ResultContent.build(ResultState.RoleNameExist);
        }
        role.setUniqueIndex(uniqueIndex);


        //更新时间
        this.dbHelper.saveTime(role);

        //入库
        this.mongoTemplate.save(role);

        return ResultContent.buildContent(role.getId());
    }

    @Override
    public Role findAndIncUserInfo(String enterpriseId, RoleModel roleModel) {

        if (!StringUtils.hasText(roleModel.getRoleName())) {
            return null;
        }


        //唯一索引
        String uniqueIndex = uniqueIndex(enterpriseId, roleModel.getRoleName());
        if (this.roleDao.existsByUniqueIndex(uniqueIndex)) {
            return null;
        }


        //查询条件
        Query query = new Query(
                Criteria.where("enterprise").is(Enterprise.build(enterpriseId)).and("roleName").is(roleModel.getRoleName())
        );


        //忽略列表
        Set<String> ignoreSets = new HashSet<>() {{
            add("auth");
            add("identity");
            add("userModel");
        }};
        BeanUtil.getNullPropertyNames(roleModel, ignoreSets);

        //更新条件
        Update update = new Update();
        EntityObjectUtil.entity2Update(roleModel, update, ignoreSets);

        //增量更新
        update.addToSet("auth").each(roleModel.getAuth());
        update.addToSet("identity").each(roleModel.getIdentity());
        update.set("uniqueIndex", uniqueIndex);

        //时间戳
        this.dbHelper.saveTime(update);

        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        findAndModifyOptions.returnNew(true);
        findAndModifyOptions.upsert(true);

        return this.mongoTemplate.findAndModify(query, update, findAndModifyOptions, Role.class);
    }


    @Override
    public Page<Role> list(String enterpriseId, RoleModel roleModel, Pageable pageable) {
        //企业id
        Enterprise enterprise = this.enterpriseDao.findTop1ById(enterpriseId);
        if (enterprise == null) {
            return PageEntityUtil.buildEmptyPage(pageable);
        }

        //条件查询
        Criteria criteria = Criteria.where("enterprise").is(enterprise);
        criteria = EntityObjectUtil.buildCriteria(criteria, roleModel, EntityObjectUtil.CriteriaType.Like, "roleName", "remark");
        criteria = EntityObjectUtil.buildCriteria(criteria, roleModel, EntityObjectUtil.CriteriaType.In, "auth");

        return this.dbHelper.pages(Query.query(criteria), pageable, Role.class);
    }


    @Override
    public boolean setRoleAuth(String roleId, String... auth) {

        Query query = buildRoleQueryFromRoleId(roleId);

        Update update = new Update();
        update.set("auth", auth);
        dbHelper.updateTime(update);

        return this.mongoTemplate.updateMulti(query, update, Role.class).getModifiedCount() > 0;
    }

    @Override
    public long addUserRole(String roleId, String... uid) {

        BulkWriteResult bulkWriteResult = executeUpdateUserRoleBulkOperations(roleId, uid, (BulkOperations bulkOperations, Role role, User user) -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("user").is(user).and("role").is(role));

            Update update = new Update();
            update.set("user", user);
            update.set("role", role);
            update.set("uniqueIndex", HashUtil.hash(role.getId(), user.getUid()));

            dbHelper.saveTime(update);

            bulkOperations.upsert(query, update);
        });

        return bulkWriteResult != null ? bulkWriteResult.getUpserts().size() : 0;
    }

    @Override
    public long removeUserRole(String roleId, String... uid) {
        BulkWriteResult bulkWriteResult = executeUpdateUserRoleBulkOperations(roleId, uid, (BulkOperations bulkOperations, Role role, User user) -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("user").is(user).and("role").is(role));
            bulkOperations.remove(query);
        });
        return bulkWriteResult != null ? bulkWriteResult.getDeletedCount() : 0;
    }

    @Override
    public Page<UserRole> listRoleUser(String[] roleId, Pageable pageable) {
        List<Role> roles = this.mongoTemplate.find(buildRoleQueryFromRoleId(roleId), Role.class);
        if (roles == null) {
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("role").in(roles));
        return this.dbHelper.pages(query, pageable, UserRole.class);
    }

    @Override
    public List<UserRole> listRoleUser(String roleId) {
        List<Role> roles = this.mongoTemplate.find(buildRoleQueryFromRoleId(roleId), Role.class);
        if (roles == null) {
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("role").in(roles));
        return this.mongoTemplate.find(query, UserRole.class);
    }

    @Override
    public Page<UserRole> listUserRole(String enterpriseId, String uid, Pageable pageable) {
        User user = this.userDao.findAndSaveUser(enterpriseId, uid);
        if (user == null) {
            return null;
        }
        Query query = new Query(Criteria.where("user").is(user));
        return this.dbHelper.pages(query, pageable, UserRole.class);
    }

    @Override
    public List<UserRole> listUserRole(String enterpriseId, String uid) {
        User user = this.userDao.findAndSaveUser(enterpriseId, uid);
        if (user == null) {
            return null;
        }
        Query query = new Query(Criteria.where("user").is(user));
        return this.mongoTemplate.find(query, UserRole.class);
    }

    @Override
    public List<Role> findByAuthName(String enterpriseId, String authName) {
        Enterprise enterprise = Enterprise.build(enterpriseId);

        Query query = new Query();
        query.addCriteria(Criteria.where("enterprise").is(enterprise).and("auth").in(authName));

        return this.mongoTemplate.find(query, Role.class);
    }

    @Override
    public List<Role> findByIdentity(String enterpriseId, String... identity) {
        Enterprise enterprise = Enterprise.build(enterpriseId);

        Query query = new Query();
        query.addCriteria(Criteria.where("enterprise").is(enterprise).and("identity").in(Set.of(identity)));

        return this.mongoTemplate.find(query, Role.class);
    }


    /**
     * 构建唯一索引
     *
     * @param enterpriseId
     * @param roleName
     * @return
     */
    private String uniqueIndex(String enterpriseId, String roleName) {
        return HashUtil.hash(enterpriseId, roleName);
    }


    /**
     * 执行更新用户角色,支持批量操作
     *
     * @param uid
     * @param updateUserRoleBulkOperations
     * @return
     */
    private BulkWriteResult executeUpdateUserRoleBulkOperations(String roleId, String[] uid, UpdateUserRoleBulkOperations updateUserRoleBulkOperations) {

        //角色中的空用户id
        if (uid == null || uid.length == 0) {
            return null;
        }

        //角色不存在
        Role role = this.mongoTemplate.findById(roleId, Role.class);
        if (role == null) {
            return null;
        }

        //企业id
        String enterpriseId = role.getEnterprise().getId();
        int setup = 0;
        BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, UserRole.class);
        for (String u : uid) {
            User user = this.userDao.findAndSaveUser(enterpriseId, u);
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
     * 通过角色id创建查询
     *
     * @param roleId
     * @return
     */
    private static Query buildRoleQueryFromRoleId(String... roleId) {
        return new Query(Criteria.where("_id").in(Set.of(roleId)));
    }


    /**
     * 更新用户角色的接口
     */
    @FunctionalInterface
    interface UpdateUserRoleBulkOperations {
        void execute(BulkOperations bulkOperations, Role role, User user);
    }


}
