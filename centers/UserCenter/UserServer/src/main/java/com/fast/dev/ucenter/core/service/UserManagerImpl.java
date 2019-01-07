package com.fast.dev.ucenter.core.service;

import com.fast.dev.ucenter.core.dao.BaseUserDao;
import com.fast.dev.ucenter.core.dao.UserTokenDao;
import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.domain.ServiceToken;
import com.fast.dev.ucenter.core.domain.UserToken;
import com.fast.dev.ucenter.core.helper.password.PassWordHelper;
import com.fast.dev.ucenter.core.model.BaseUserModel;
import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.model.UserRegisterModel;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.core.type.PassWordEncodeType;
import com.fast.dev.ucenter.core.type.TokenState;
import com.fast.dev.ucenter.core.type.UserLoginType;
import com.fast.dev.ucenter.core.util.BaseTokenUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserManagerImpl extends BaseUserService implements UserManagerService {

    @Autowired
    private UserTokenDao userTokenDao;

    @Autowired
    private BaseUserDao baseUserDao;

    @Autowired
    private UserService userService;

    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    private PassWordHelper passWordHelper;

    @Override
    public UserTokenModel queryByUserToken(String token) {
        UserToken userToken = this.userTokenDao.queryOnly(token);
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
    public BaseUserModel updateLoginValue(String uid, UserLoginType loginType, String loginName) {
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


}
