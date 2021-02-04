package demo.simple.dao.impl;

import com.fast.dev.data.base.data.DataHelper;
import demo.simple.dao.UserDao;
import demo.simple.dao.extend.UserDaoExtend;
import demo.simple.domain.User;
import demo.simple.domain.UserGroup;
import demo.simple.domain.UserInfo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;

@Log
public class UserDaoImpl implements UserDaoExtend {


    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private DataHelper dataHelper;


    @Autowired
    private UserDao userDao;


    @Override
    public void updateUser(String name, long time) {
        Query query = new Query().addCriteria(Criteria.where("name").is(name));
        Update update = new Update();
        update.set("time", new BigDecimal(time));
        this.mongoTemplate.updateMulti(query, update, User.class);
    }

    @Override
    public Object dataUpdate(String uid, String userName) {

        if (!this.userDao.existsById(uid)) {
            return null;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(uid));

        Update update = new Update();
        update.set("userName", userName);
        this.mongoTemplate.updateFirst(query, update, User.class);

        User user = this.mongoTemplate.findById(uid, User.class);

        //创建userinfo
        this.mongoTemplate.upsert(new Query().addCriteria(Criteria.where("uid").is(uid)), new Update().setOnInsert("uid", uid), UserInfo.class);

        //创建UserGroup
        this.mongoTemplate.upsert(new Query().addCriteria(Criteria.where("master").is(user)), new Update().setOnInsert("master", user), UserGroup.class);


        return uid;

    }


}

