package com.fast.dev.ucenter.core.dao.redis;

import com.fast.dev.ucenter.core.domain.BaseToken;
import com.fast.dev.ucenter.core.domain.ServiceToken;
import com.fast.dev.ucenter.core.domain.StrongServiceToken;
import com.fast.dev.ucenter.core.domain.UserToken;

import java.util.Set;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 * 用户令牌dao
 */
public interface UserTokenDao {


    /**
     * 创建登陆令牌
     *
     * @param expireTime
     */
    boolean createUserToken(UserToken userToken, long expireTime);


    /**
     * 创建注册令牌
     */
    boolean createServiceToken(ServiceToken serviceToken, long expireTime);


    /**
     * 创建增强令牌
     */
    boolean createStrongServiceToken(StrongServiceToken strongServiceToken, long expireTime);


    /**
     * 查询令牌
     *
     * @param token
     * @param <T>
     * @return
     */
    <T extends BaseToken> T query(String token);


    /**
     * 查询用户id的令牌
     *
     * @param uid
     * @return
     */
    Set<BaseToken> findByUid(String uid);


    /**
     * 仅仅查询不做修改操作
     *
     * @param token
     * @param <T>
     * @return
     */
    <T extends BaseToken> T queryOnly(String token);


    /**
     * 删除令牌
     *
     * @param token
     * @return
     */
    boolean remove(String token);


}
