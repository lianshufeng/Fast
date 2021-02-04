package demo.simple.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import demo.simple.dao.extend.UserDaoExtend;
import demo.simple.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface UserDao extends MongoDao<User>, UserDaoExtend {


    /**
     * 查询用户名与随机数
     *
     * @param name
     * @param r
     * @return
     */
    List<User> findByUserNameAndR(String name, int r);


    /**
     * 通过用户名查询所有的用户
     *
     * @param name
     * @return
     */
    List<User> queryUserByUserName(String name);


    /**
     * 查询用户名
     *
     * @param name
     * @return
     */
    long countByUserName(String name);


    /**
     * 删除用户
     *
     * @param name
     * @param r
     * @return
     */
    List<User> removeByUserNameAndR(String name, int r);


    @Query("{ 'userName': ?0, 'r': ?1}")
    List<User> getUser(String name, int r);


    /**
     * 分页查询
     *
     * @param name
     * @param pageable
     * @return
     */
    Page<User> findByUserName(String name, Pageable pageable);


    boolean  existsById(String uid);




}
