package com.fast.dev.user.auth.scheduler;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.data.base.data.DataHelper;
import com.fast.dev.data.base.data.model.UpdateDataDetails;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.user.auth.conf.UserRoleAuthConf;
import com.fast.dev.user.auth.dao.RoleDao;
import com.fast.dev.user.auth.dao.UserIdentityUpdateListDao;
import com.fast.dev.user.auth.domain.User;
import com.fast.dev.user.auth.domain.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@EnableScheduling
public class UserIdentityUpdateScheduler {

    private final static int onecLimit = 100;

    @Autowired
    private UserIdentityUpdateListDao userIdentityUpdateListDao;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserRoleAuthConf userRoleAuthConf;


    @Autowired
    private DBHelper dbHelper;


    @Autowired
    private DataHelper dataHelper;


    @Scheduled(cron = "*/5 * * * * ?")
    public void updateUserIdentity() {
        Set<String> userIds = this.userIdentityUpdateListDao.findAndeRemoveUpdateUser(onecLimit);
        if (userIds == null || userIds.size() == 0) {
            return;
        }

        //开始更新
        updateUserIdentity(userIds);


    }


    /**
     * 更新用户的身份
     *
     * @param userIds
     */
    private void updateUserIdentity(Set<String> userIds) {

        // 批量更新
        long count = 0;
        BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, User.class);
        for (String userId : userIds) {

            List<UserRole> roles = this.roleDao.listUserRole(userId);
            if (roles == null) {
                continue;
            }

            //取出所有角色的身份
            Set<String> identitySets = roles.stream().map((it) -> {
                return it.getRole().getIdentity();
            }).collect(Collectors.toSet());

            //移除空身份
            identitySets.remove("");
            identitySets.remove(null);

            //更新用户的身份标识
            Query query = Query.query(Criteria.where("_id").is(userId));
            Update update = new Update();
            if (identitySets.size() == 0) {
                update.unset("identities");
            } else {
                update.set("identities", identitySets);
            }


            this.dbHelper.updateTime(update);

            bulkOperations.updateOne(query, update);

            count++;
        }

        int updateSize = 0;
        if (count > 0) {
            updateSize = bulkOperations.execute().getModifiedCount();
        }

        if (updateSize > 0) {
            log.info("update user  identity: " + updateSize);
        }


        //同步用户数据到其他数据中
        if (userIds != null && userIds.size() > 0) {
            updateUserInfo(userIds);
        }


    }


    /**
     * 同步用户数据
     */
    private void updateUserInfo(Set<String> userIds) {
        Map<String, Integer> ret = new HashMap<>();
        //同步数据
        for (String userId : userIds) {
            UpdateDataDetails[] updateDataDetails = this.dataHelper.update(this.userRoleAuthConf.getUserEntityCls(), userId);
            if (updateDataDetails == null) {
                continue;
            }

            //进行分类相加
            for (UpdateDataDetails updateDataDetail : updateDataDetails) {
                String collectionName = this.dbHelper.getCollectionName(updateDataDetail.getEntity());
                if (ret.containsKey(collectionName)) {
                    ret.put(collectionName, ret.get(collectionName) + updateDataDetail.getIds().length);
                } else {
                    ret.put(collectionName, updateDataDetail.getIds().length);
                }
            }
        }
        log.info("update user info: " + JsonUtil.toJson(ret));
    }


}
