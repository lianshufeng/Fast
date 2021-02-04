package demo.simple.service;

import com.fast.dev.data.base.util.PageEntityUtil;
import demo.simple.dao.UserDao;
import demo.simple.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;


    /**
     * JPA 保存，自动生成更新时间与创建时间
     *
     * @param userName
     * @return
     */
    public Object save(String userName) {
        User user = new User();
        user.setUserName(userName);
        return this.userDao.save(user);
    }


    /**
     * Jpa查询
     *
     * @param id
     * @return
     */
    public Object findJpaId(Long id) {
        return this.userDao.findById(id);
    }


    /**
     * 通过扩展接口查询
     *
     * @param id
     * @return
     */
    public Object findExtendId(Long id) {
        return this.userDao.findOneFromId(id);
    }


    /**
     * @param userName
     * @param id
     * @return
     */
    public Object findByUserNameAndId(String userName, Long id) {
        return this.userDao.findByUserNameAndId(userName, id);
    }

    /**
     * 分页查询
     *
     * @return
     */
    public Object listAge(int age, Pageable pageable) {
        return PageEntityUtil.toPageModel(this.userDao.listAge(age, pageable), new PageEntityUtil.DataClean<User, Map>() {
            @Override
            public Map execute(User data) {
                return new HashMap(){{
                    put("userName",data.getUserName());
                }};
            }
        });
    }


    /**
     * 更新age , 事务
     *
     * @param oldAge
     * @param newAge
     * @return
     */
    @Transactional
    public Object updateAge(int oldAge, int newAge) {
        int size = this.userDao.update(oldAge, newAge);
        return size;
    }


    /**
     *
     * @return
     */
    @Transactional
    public Object group(){
        return this.userDao.group();
    }

}
