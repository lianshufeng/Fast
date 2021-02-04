package com.fast.dev.auth.center.server.core.dao.impl;

import com.fast.dev.auth.center.server.core.conf.UserLogConf;
import com.fast.dev.auth.center.server.core.dao.extend.UserLogDaoExtend;
import com.fast.dev.auth.center.server.core.domain.UserLog;
import com.fast.dev.auth.client.log.model.UserLogModel;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.util.Date;

public class UserLogDaoImpl implements UserLogDaoExtend {

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserLogConf userLogConf;

    @Override
    public boolean add(UserLogModel... userLogModel) {
        if (userLogModel == null || userLogModel.length == 0) {
            return false;
        }

        BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, UserLog.class);
        for (UserLogModel logModel : userLogModel) {
            UserLog userLog = new UserLog();
            BeanUtils.copyProperties(logModel, userLog);
            //设置数据过期时间
            userLog.setTTL(new Date(this.dbHelper.getTime() + userLogConf.getTTL()));
            this.dbHelper.saveTime(userLog);

            bulkOperations.insert(userLog);
        }
        return bulkOperations.execute().getInsertedCount() > 0;

    }

    @Override
    public Page<UserLog> list(UserLogModel userLog, Pageable pageable) {
        //条件查询
        Criteria criteria = EntityObjectUtil.buildCriteria(new Criteria(), userLog, EntityObjectUtil.CriteriaType.Like, "ua", "ip", "uid", "action", "method");
        return this.dbHelper.pages(Query.query(criteria), pageable, UserLog.class);
    }
}
