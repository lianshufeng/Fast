package com.fast.dev.ucenter.core.dao;

import com.fast.dev.ucenter.core.domain.BaseToken;
import com.fast.dev.ucenter.core.domain.ServiceToken;
import com.fast.dev.ucenter.core.domain.UserToken;

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
    public boolean createUserToken(UserToken userToken, long expireTime);


    /**
     * 创建注册令牌
     */
    public boolean createServiceToken(ServiceToken serviceToken, long timeOut);


    /**
     * 查询令牌
     *
     * @param token
     * @param <T>
     * @return
     */
    public <T extends BaseToken> T query(String token);


    /**
     * 仅仅查询不做修改操作
     *
     * @param token
     * @param <T>
     * @return
     */
    public <T extends BaseToken> T queryOnly(String token);


    /**
     * 删除令牌
     *
     * @param token
     * @return
     */
    public boolean remove(String token);


}
