package com.fast.dev.ucenter.core.service;

import com.fast.dev.ucenter.core.dao.BaseUserDao;
import com.fast.dev.ucenter.core.dao.UserTokenDao;
import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.domain.UserToken;
import com.fast.dev.ucenter.core.helper.password.PassWordHelper;
import com.fast.dev.ucenter.core.model.*;
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
        BaseUserModel baseUserModel = new BaseUserModel();
        BeanUtils.copyProperties(baseUser, baseUserModel);
        return baseUserModel;
    }

    @Override
    public BaseUserModel queryByLoginName(UserLoginType loginType, String loginName) {
        BaseUser baseUser = this.baseUserDao.queryByLoginName(loginType, loginName);
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
        if (userLoginType == UserLoginType.Phone) {
            baseUser.setPhone(loginName);

        } else if (userLoginType == UserLoginType.UserName) {
            baseUser.setUserName(loginName);
        }
        // 设置密码
        updateBaseUserPassWord(baseUser, passWord);
        //注册新用户入库
        createRegisterUser(baseUser);
        TokenState state = baseUser.getId() == null ? TokenState.Error : TokenState.Success;
        return new UserRegisterModel(state, baseUser.getId());
    }

    @Override
    public UserTokenModel login(UserLoginType loginType, String loginName, String passWord, Long expireTime, TokenEnvironment env) {
        UserLoginToken userLoginToken = userService.getUserLoginToken(loginType, loginName, env);
        String token = userLoginToken.getToken();
        String code = userLoginToken.getRobotValidate().getData();
        return userService.login(env, token, code, passWord, expireTime);
    }
}
