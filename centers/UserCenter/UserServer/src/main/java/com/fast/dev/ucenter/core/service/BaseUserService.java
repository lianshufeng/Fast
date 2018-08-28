package com.fast.dev.ucenter.core.service;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.ucenter.core.dao.BaseUserDao;
import com.fast.dev.ucenter.core.dao.UserTokenDao;
import com.fast.dev.ucenter.core.domain.BaseToken;
import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.domain.UserToken;
import com.fast.dev.ucenter.core.helper.UserPushMessageHelper;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.core.util.BaseTokenUtil;
import com.fast.dev.ucenter.core.util.RandomUtil;
import com.fast.dev.ucenter.core.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 通用用户的业务
 */
public class BaseUserService {

    @Autowired
    private UserTokenDao userTokenDao;


    @Autowired
    private UserPushMessageHelper userPushMessageHelper;


    @Autowired
    private DBHelper dbHelper;


    @Autowired
    private BaseUserDao baseUserDao;

    /**
     * 用户注销
     *
     * @param token
     * @return
     */
    public boolean logout(String token) {
        BaseToken baseToken = this.userTokenDao.queryOnly(token);
        if (baseToken == null || !(baseToken instanceof UserToken)) {
            return false;
        }
        UserToken userToken = (UserToken) baseToken;
        boolean flag = this.userTokenDao.remove(token);
        if (flag) {
            //删除令牌后消息总线通
            this.userPushMessageHelper.pushLogoutMsg(userToken);
        }
        return flag;
    }


    /**
     * 用户登陆
     *
     * @param baseUser
     * @return
     */
    public UserTokenModel createUserToken(BaseUser baseUser, long timeOut) {
        // 用户令牌并入库
        UserToken userToken = new UserToken();
        userToken.setId(RandomUtil.uuid());
        this.dbHelper.saveTime(userToken);
        userToken.setUid(baseUser.getId());
        userToken.setSecret(TokenUtil.create());
        userToken.setToken(TokenUtil.create());
        UserTokenModel userTokenModel = null;
        if (this.userTokenDao.createUserToken(userToken, timeOut)) {
            userTokenModel = BaseTokenUtil.toUserTokenModel(userToken);
            this.userPushMessageHelper.pushLoginMsg(userToken);
        }
        return userTokenModel;
    }


    /**
     * 用户注册
     *
     * @param baseUser
     * @return
     */
    public void createRegisterUser(BaseUser baseUser) {
        this.dbHelper.saveTime(baseUser);
        this.baseUserDao.save(baseUser);
        this.userPushMessageHelper.pushRegisterMsg(baseUser.getId());
    }

}
