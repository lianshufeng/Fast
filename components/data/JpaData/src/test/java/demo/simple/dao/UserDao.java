package demo.simple.dao;

import com.fast.dev.data.jpa.Dao.JpaDataDao;
import demo.simple.dao.extend.UserDaoExtend;
import demo.simple.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 用户dao
 */
public interface UserDao extends JpaDataDao<User>, UserDaoExtend {


    /**
     * JPA查询
     *
     * @param userName
     * @param id
     * @return
     */
    public User findByUserNameAndId(String userName, Long id);


    /**
     * 分页查询所有
     *
     * @param pageable
     * @return
     */
    @Query("select u from User u where age = ?1 ")
    public Page<User> listAge(int age, Pageable pageable);


    /**
     * 更新必须 @Modifying
     * @param oldAge
     * @param newAge
     * @return
     */
    @Modifying
    @Query("update User u set u.age = ?2 where u.age = ?1")
    public int update(int oldAge,int newAge );

}
