package demo.simple.dao.extend;

public interface UserDaoExtend {

    void updateUser(String name, long time);


    /**
     * 更新用户
     *
     * @param uid
     * @param userName
     */
    Object dataUpdate(String uid, String userName);


}
