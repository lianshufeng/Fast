package demo.simple.dao.extend;

import demo.simple.domain.User;

public interface UserDaoExtend {


    /**
     * 通过ID查询该对象
     * @param id
     * @return
     */
    public User findOneFromId(Long id);

    /**
     * 插入一个用户
     * @return
     */
    public User group();

}
