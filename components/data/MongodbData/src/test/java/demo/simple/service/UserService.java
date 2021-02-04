package demo.simple.service;

import com.fast.dev.data.base.data.DataHelper;
import com.fast.dev.data.base.data.DataSyncHelper;
import com.mongodb.client.result.UpdateResult;
import demo.simple.dao.UserDao;
import demo.simple.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;


    @Autowired
    MongoTemplate mongoTemplate;


    /**
     * 保存数据
     *
     * @param userName
     * @return
     */
//    @Transactional
    public String save(String userName) {
        User user = new User();
        user.setBirthdayTime(new Date(1989, 7, 25).getTime());
        user.setUserName(userName);
        user.setR(new Random().nextInt(10));
        this.userDao.save(user);
        return user.getId();
    }


    /**
     * 查询用户
     *
     * @param name
     * @param r
     * @return
     */
    public List<User> findUser(String name, int r) {
        return this.userDao.findByUserNameAndR(name, r);
    }


    /**
     * 查询所用用户
     *
     * @param name
     * @return
     */
    public List<User> query(String name) {
        return this.userDao.queryUserByUserName(name);
    }


    /**
     * 统计所有用户
     *
     * @param name
     * @return
     */
    public long count(String name) {
        return this.userDao.countByUserName(name);
    }


    /**
     * 删除指定条件的用户
     *
     * @param name
     * @param r
     * @return
     */
    public List<User> remove(String name, int r) {
        return this.userDao.removeByUserNameAndR(name, r);
    }


    /**
     * 获取用户
     *
     * @param name
     * @param r
     * @return
     */
    public List<User> get(String name, int r) {
        return this.userDao.getUser(name, r);
    }


    public Page<User> list(String name, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "r");
//        Page<User> users = this.userDao.findAll(pageRequest);
        return this.userDao.findByUserName(name, pageRequest);
    }


    public Object update(String name, int r, int newR) {
        Query query = new Query().addCriteria(Criteria.where("userName").is(name).and("r").is(r));
        Update update = new Update();
        update.set("r", newR);
        UpdateResult updateResult = this.mongoTemplate.updateMulti(query, update, User.class);
        return updateResult.getModifiedCount();
    }


    @Transactional
    public Object transactional(int n) {
        boolean isReturn = false;
        if (System.currentTimeMillis() % 2 == 0) {
            isReturn = true;

        }
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            User u = new User("test_" + System.currentTimeMillis() + "_" + i);
            this.mongoTemplate.insert(u);
            ids.add(u.getId());
            if (isReturn) {
                throw new RuntimeException("error");
            }
        }
        return ids;
    }


    @Autowired
    private DataSyncHelper dataHelper;

//    private DataSyncHelper dataSyncHelper


    @Transactional
    public Object dataUpdate(String uid, String userName) {
        //数据更新
        this.userDao.dataUpdate(uid, userName);
        //同步数据
        return this.dataHelper.update(User.class, uid);

    }


}
