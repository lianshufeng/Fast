package com.fast.dev.ucenter.core.service;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.ucenter.core.conf.ValidateDataConf;
import com.fast.dev.ucenter.core.dao.BaseUserDao;
import com.fast.dev.ucenter.core.dao.UserTokenDao;
import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.domain.ServiceToken;
import com.fast.dev.ucenter.core.domain.UserToken;
import com.fast.dev.ucenter.core.helper.ValidateDataHelper;
import com.fast.dev.ucenter.core.model.*;
import com.fast.dev.ucenter.core.type.*;
import com.fast.dev.ucenter.core.util.PassWordUtil;
import com.fast.dev.ucenter.core.util.RandomUtil;
import com.fast.dev.ucenter.core.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 */
@Service
public class UserServiceImpl extends BaseUserService implements UserService {

    @Autowired
    private BaseUserDao baseUserDao;


    @Autowired
    private UserTokenDao userTokenDao;


    @Autowired
    private DBHelper dbHelper;


    @Autowired
    private ValidateDataHelper validateDataHelper;


    @Override
    public UserLoginToken getUserLoginToken(UserLoginType userLoginType, String loginName, TokenEnvironment tokenEnvironment) {

        //不支持的类型
        if (userLoginType.getRegisterService() == null) {
            return new UserLoginToken(TokenState.NotSupportType);
        }

        //判断用户是否存在
        if (!existsUser(userLoginType, loginName)) {
            return new UserLoginToken(TokenState.UserNotExist);
        }

        // 机器校验
        RobotValidate robotValidate = new RobotValidate(userLoginType.getValidateType());
        String code = createRobotValidate(tokenEnvironment, robotValidate, userLoginType.getLoginService(), loginName);


        //  创建业务令牌
        ServiceToken serviceToken = createServiceToken(tokenEnvironment, userLoginType.getLoginService(), loginName, code, robotValidate);
        if (serviceToken == null) {
            return new UserLoginToken(TokenState.CreateError);
        }

        //返回用户登陆令牌
        UserLoginToken userLoginToken = new UserLoginToken(TokenState.Success);
        userLoginToken.setRobotValidate(robotValidate);
        userLoginToken.setToken(serviceToken.getToken());
        return userLoginToken;
    }


    @Override
    public UserTokenModel login(TokenEnvironment env, String token, String validateCode, String passWord, long expireTime) {
        //取出业务令牌并验证码校验
        ServiceToken serviceToken = this.userTokenDao.query(token);
        TokenState tokenState = validateToken(serviceToken, validateCode,true);
        if (tokenState != null) {
            return new UserTokenModel(tokenState);
        }

        //判断业务令牌是否合法
        if (serviceToken.getServiceTokenType().getServiceType() != ServiceType.Login) {
            return new UserTokenModel(TokenState.TokenNotMatch);
        }


        //根据登陆方式取用户信息
        BaseUser baseUser = findBaseUser(serviceToken);
        if (baseUser == null) {
            return new UserTokenModel(TokenState.UserNotExist);
        }

        //密码校验
        if (!PassWordUtil.validate(baseUser.getSalt(), passWord, baseUser.getPassWord())) {
            return new UserTokenModel(TokenState.PassWordError);
        }

        //删除登陆令牌
        this.userTokenDao.remove(token);


        //创建用户令牌（入库并返回令牌对象）
        return createUserToken(env, baseUser, expireTime);
    }


    @Override
    public UserRegisterToken getUserRegisterToken(UserLoginType userLoginType, String loginName, TokenEnvironment loginEnvironment) {

        //不支持的类型
        if (userLoginType.getRegisterService() == null) {
            return new UserRegisterToken(TokenState.NotSupportType);
        }

        //用户是否存在
        if (existsUser(userLoginType, loginName)) {
            return new UserRegisterToken(TokenState.UserExist);
        }

        //生成机器校验码
        RobotValidate robotValidate = new RobotValidate(userLoginType.getValidateType());
        String code = createRobotValidate(loginEnvironment, robotValidate, userLoginType.getRegisterService(), loginName);


        //创建业务令牌
        ServiceToken serviceToken = createServiceToken(loginEnvironment, userLoginType.getRegisterService(), loginName, code, robotValidate);
        if (serviceToken == null) {
            return new UserRegisterToken(TokenState.CreateError);
        }

        UserRegisterToken userRegisterToken = new UserRegisterToken(TokenState.Success);
        userRegisterToken.setRobotValidate(robotValidate);
        userRegisterToken.setToken(serviceToken.getToken());
        return userRegisterToken;
    }

    @Override
    public UserRegisterModel register(TokenEnvironment env, String token, String validateCode, String passWord) {
        //校验业务令牌的验证码
        ServiceToken serviceToken = this.userTokenDao.query(token);
        TokenState tokenState = validateToken(serviceToken, validateCode,true);
        if (tokenState != null) {
            return new UserRegisterModel(tokenState);
        }

        //业务令牌类型匹配判断
        if (serviceToken.getServiceTokenType().getServiceType() != ServiceType.Register) {
            return new UserRegisterModel(TokenState.TokenNotMatch);
        }

        //删除这个业务令牌
        this.userTokenDao.remove(serviceToken.getToken());

        //  实例化对象
        BaseUser baseUser = newBaseUser(serviceToken, passWord);
        if (baseUser == null) {
            return new UserRegisterModel(TokenState.Error);
        }

        //注册新用户入库
        createRegisterUser(baseUser);

        TokenState state = baseUser.getId() == null ? TokenState.Error : TokenState.Success;
        return new UserRegisterModel(state, baseUser.getId());
    }





    @Override
    public boolean ping(String uToken) {
        UserToken userToken = userTokenDao.queryOnly(uToken);
        return userToken != null;
    }


    @Override
    public UserFastToken getFastToken(String phone, TokenEnvironment tokenEnvironment) {

        // 机器校验
        RobotValidate robotValidate = new RobotValidate(ValidateType.Sms);
        String code = createRobotValidate(tokenEnvironment, robotValidate, ServiceTokenType.FastLogin, phone);


        //  创建业务令牌
        ServiceToken serviceToken = createServiceToken(tokenEnvironment, ServiceTokenType.FastLogin, phone, code, robotValidate);
        if (serviceToken == null) {
            return new UserFastToken(TokenState.CreateError);
        }

        //返回快捷令牌
        UserFastToken userFastToken = new UserFastToken(TokenState.Success);
        userFastToken.setRobotValidate(robotValidate);
        userFastToken.setToken(serviceToken.getToken());
        return userFastToken;
    }

    @Override
    public UserTokenModel fast(TokenEnvironment env, String token, String validateCode, long expireTime) {
        //查询令牌并判断校验码是否有效
        ServiceToken serviceToken = this.userTokenDao.query(token);
        TokenState tokenState = validateToken(serviceToken, validateCode,true);
        if (tokenState != null) {
            return new UserTokenModel(tokenState);
        }

        //判断业务令牌是否合法
        if (serviceToken.getServiceTokenType() != ServiceTokenType.FastLogin) {
            return new UserTokenModel(TokenState.TokenNotMatch);
        }

        //根据登陆方式取用户信息
        BaseUser baseUser = findAndSaveBaseUser(serviceToken);

        //删除登陆令牌
        this.userTokenDao.remove(token);

        //创建用户令牌（入库并返回令牌对象）
        return createUserToken(env, baseUser, expireTime);
    }

    @Override
    public UpdatePassWordToken getUpdatePassWordToken(UserLoginType userLoginType, String loginName, TokenEnvironment env) {

        //不支持的类型
        if (userLoginType.getRegisterService() == null) {
            return new UpdatePassWordToken(TokenState.NotSupportType);
        }

        //用户是否存在
        if (!existsUser(userLoginType, loginName)) {
            return new UpdatePassWordToken(TokenState.UserNotExist);
        }

        //生成机器校验码
        RobotValidate robotValidate = new RobotValidate(userLoginType.getValidateType());
        String code = createRobotValidate(env, robotValidate, userLoginType.getUpdatePassWordService(), loginName);


        //创建业务令牌
        ServiceToken serviceToken = createServiceToken(env, userLoginType.getUpdatePassWordService(), loginName, code, robotValidate);
        if (serviceToken == null) {
            return new UpdatePassWordToken(TokenState.CreateError);
        }

        UpdatePassWordToken updatePassWordToken = new UpdatePassWordToken(TokenState.Success);
        updatePassWordToken.setRobotValidate(robotValidate);
        updatePassWordToken.setToken(serviceToken.getToken());

        return updatePassWordToken;
    }

    @Override
    public TokenState updatePassWord(String token, String code, String passWord, String newPassWord, TokenEnvironment env) {

        //校验业务令牌的验证码
        ServiceToken serviceToken = this.userTokenDao.query(token);
        TokenState tokenState = validateToken(serviceToken, code,true);
        if (tokenState != null) {
            return tokenState;
        }

        //业务令牌类型匹配判断
        if (serviceToken.getServiceTokenType().getServiceType() != ServiceType.UpdatePassWord) {
            return TokenState.TokenNotMatch;
        }


        //  修改密码
        tokenState = super.updateUserPassWord(serviceToken, passWord, newPassWord);


        //删除这个业务令牌
        if (tokenState == TokenState.Success) {
            this.userTokenDao.remove(serviceToken.getToken());
        }

        return tokenState;
    }



}
