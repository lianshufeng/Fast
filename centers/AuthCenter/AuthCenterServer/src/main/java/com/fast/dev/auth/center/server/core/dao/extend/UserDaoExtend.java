package com.fast.dev.auth.center.server.core.dao.extend;

import com.fast.dev.auth.center.server.core.domain.Enterprise;
import com.fast.dev.auth.center.server.core.domain.User;
import com.fast.dev.auth.center.server.core.model.FamilyModel;
import com.fast.dev.auth.client.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface UserDaoExtend {

    /**
     * 查询或保存用户
     *
     * @param enterpriseId
     * @param uid
     * @return
     */
    User findAndSaveUser(String enterpriseId, String uid);


    /**
     * 更新用户信息
     *
     * @param enterpriseId
     * @param userModel
     * @return
     */
    String updateUser(String enterpriseId, UserModel userModel);


    /**
     * 查询用户所在的所有企业列表
     *
     * @param uid
     * @return
     */
    Page<Enterprise> getEnterprise(String uid, Pageable pageable);


    /**
     * 查询用户附属的企业
     *
     * @param uid
     * @return
     */
    List<Enterprise> getAffiliatedEnterprises(String uid);


    /**
     * 用户中添加角色
     *
     * @param uid
     * @param roleId
     */
    Set<String> addRole(String uid, String... roleId);


    /**
     * 删除用户的角色
     *
     * @param uid
     * @param roleId
     * @return
     */
    Set<String> removeRole(String uid, String... roleId);


    /**
     * 删除角色的info
     *
     * @param enterpriseId
     * @param uid
     * @param key
     * @return
     */
    boolean removeUserInfo(String enterpriseId, String uid, String... key);


    /**
     * 条件用户企业
     *
     * @param pageable
     * @param mql
     * @return
     */
    Page<User> queryEnterpriseUser(String mql, Pageable pageable);


    /**
     * 设置用户家庭组信息
     *
     * @param epId
     * @param uid
     */
    void setUserFamily(FamilyModel familyModel, String epId, String... uid);


    /**
     * 重置Info的索引
     */
    void reIndexInfo();


    /**
     * 更新冗余的用户手机号码
     *
     * @param uid
     * @param phone
     */
    void updateUserPhone(String uid, String phone);


}
