package com.fast.dev.ucenter.core.service.impl;

import com.fast.dev.data.base.util.PageEntityUtil;
import com.fast.dev.ucenter.core.dao.mongo.BaseUserDao;
import com.fast.dev.ucenter.core.dao.mongo.BaseUserLogDao;
import com.fast.dev.ucenter.core.dao.redis.UserTokenDao;
import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.domain.BaseUserLog;
import com.fast.dev.ucenter.core.domain.ServiceToken;
import com.fast.dev.ucenter.core.domain.UserToken;
import com.fast.dev.ucenter.core.helper.password.PassWordHelper;
import com.fast.dev.ucenter.core.model.*;
import com.fast.dev.ucenter.core.service.UserManagerService;
import com.fast.dev.ucenter.core.service.UserService;
import com.fast.dev.ucenter.core.type.PassWordEncodeType;
import com.fast.dev.ucenter.core.type.TokenState;
import com.fast.dev.ucenter.core.type.UserLoginType;
import com.fast.dev.ucenter.core.util.BaseTokenUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserManagerImpl extends BaseUserService implements UserManagerService {

    @Autowired
    private UserTokenDao userTokenDao;

    @Autowired
    private BaseUserDao baseUserDao;

    @Autowired
    private BaseUserLogDao baseUserLogDao;

    @Autowired
    private UserService userService;

    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    private PassWordHelper passWordHelper;

    @Override
    public UserTokenModel queryByUserToken(String token) {
        UserToken userToken = this.userTokenDao.query(token);
        return BaseTokenUtil.toUserTokenModel(userToken);
    }


    @Override
    public BaseUserModel queryUserId(String uid) {
        BaseUser baseUser = this.baseUserDao.findTop1ById(uid);
        if (baseUser == null) {
            return null;
        }
        return copyToBaseUserModel(baseUser);
    }

    @Override
    public BaseUserModel queryByLoginName(UserLoginType loginType, String loginName) {
        BaseUser baseUser = this.baseUserDao.queryByLoginName(loginType, loginName);
        if (baseUser == null) {
            return null;
        }
        return copyToBaseUserModel(baseUser);
    }


    /**
     * 拷贝用户模型
     *
     * @param baseUser
     * @return
     */
    private BaseUserModel copyToBaseUserModel(BaseUser baseUser) {
        if (baseUser == null) {
            return null;
        }
        BaseUserModel baseUserModel = new BaseUserModel();
        BeanUtils.copyProperties(baseUser, baseUserModel);
        return baseUserModel;
    }

    @Override
    public UserRegisterModel addUser(UserLoginType userLoginType, String loginName, String passWord) {
        //不支持的类型
        if (userLoginType.getRegisterService() == null) {
            return new UserRegisterModel(TokenState.NotSupportType);
        }
        //用户是否存在
        if (existsUser(userLoginType, loginName)) {
            return new UserRegisterModel(TokenState.UserExist);
        }
        BaseUser baseUser = new BaseUser();
        super.setBaseUserLoginName(baseUser, userLoginType, loginName);
        // 设置密码
        updateBaseUserPassWord(baseUser, passWord);
        //注册新用户入库
        createRegisterUser(baseUser);
        TokenState state = baseUser.getId() == null ? TokenState.Error : TokenState.Success;
        return new UserRegisterModel(state, baseUser.getId());
    }

    @Override
    public UserTokenModel login(UserLoginType loginType, String loginName, String passWord, Long expireTime, TokenEnvironment env) {

        //构建业务令牌
        ServiceToken serviceToken = new ServiceToken();
        serviceToken.setLoginName(loginName);
        serviceToken.setServiceTokenType(loginType.getLoginService());


        //根据登陆方式取用户信息
        BaseUser baseUser = findBaseUser(serviceToken);
        if (baseUser == null) {
            return new UserTokenModel(TokenState.UserNotExist);
        }

        //密码校验
        if (!passWordHelper.validate(baseUser.getSalt(), passWord, baseUser.getPassWord(), baseUser.getPassWordEncodeType())) {
            return new UserTokenModel(TokenState.PassWordError);
        }

        //创建用户令牌（入库并返回令牌对象）
        return createUserToken(env, baseUser, expireTime);
    }

    @Override
    public UserTokenModel createToken(String uid, Long expireTime, TokenEnvironment env) {
        BaseUser baseUser = this.baseUserDao.findTop1ById(uid);
        //用户不存在
        if (baseUser == null) {
            return new UserTokenModel(TokenState.UserNotExist);
        }
        return this.baseUserService.createUserToken(env, baseUser, expireTime);
    }


    @Override
    public UserRegisterModel insertBaseUser(UserLoginType loginType, String loginName, String salt, String passWord, PassWordEncodeType encodeType) {
        //查询用户是否存在
        if (this.baseUserDao.existsByLoginName(loginType, loginName)) {
            return UserRegisterModel.builder().state(TokenState.UserExist).build();
        }

        BaseUser baseUser = new BaseUser();
        super.setBaseUserLoginName(baseUser, loginType, loginName);
        baseUser.setPassWord(passWord);
        baseUser.setSalt(salt);
        baseUser.setPassWordEncodeType(encodeType);

        createRegisterUser(baseUser);
        TokenState state = baseUser.getId() == null ? TokenState.Error : TokenState.Success;
        return new UserRegisterModel(state, baseUser.getId());
    }


    @Override
    @Transactional
    public BaseUserModel updateLoginName(String uid, UserLoginType loginType, String loginName) {
        BaseUser baseUser = this.baseUserDao.updateLoginValue(uid, loginType, loginName);
        return copyToBaseUserModel(baseUser);
    }


    @Override
    public TokenState setUserPassWord(String uid, String passWord) {
        BaseUser baseUser = this.baseUserDao.findTop1ById(uid);
        if (baseUser == null || baseUser.getId() == null) {
            return TokenState.UserNotExist;
        }
        this.updateBaseUserPassWord(baseUser, passWord);
        return this.baseUserDao.updatePassWord(baseUser.getId(), baseUser.getSalt(), baseUser.getPassWord()) ? TokenState.Success : TokenState.Error;
    }

    @Override
    public Page<BaseUserLogModel> listUserUpdateLoginName(String uid, Pageable pageable) {
        BaseUser baseUser = new BaseUser();
        baseUser.setId(uid);

        Page<BaseUserLog> pages = this.baseUserLogDao.findByBaseUser(baseUser, pageable);
        if (pages != null) {
            return PageEntityUtil.toPageModel(pages, new PageEntityUtil.DataClean<BaseUserLog, BaseUserLogModel>() {
                @Override
                public BaseUserLogModel execute(BaseUserLog data) {
                    //对象拷贝
                    BaseUserLogModel baseUserLogModel = new BaseUserLogModel();
                    BeanUtils.copyProperties(data, baseUserLogModel);
                    baseUserLogModel.setUid(data.getBaseUser().getId());
                    return baseUserLogModel;
                }
            });

        }
        return null;
    }


}
