package com.fast.dev.ucenter.core.service;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.ucenter.core.conf.UserCenterConf;
import com.fast.dev.ucenter.core.dao.BaseUserDao;
import com.fast.dev.ucenter.core.dao.UserTokenDao;
import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.domain.ServiceToken;
import com.fast.dev.ucenter.core.domain.UserToken;
import com.fast.dev.ucenter.core.model.*;
import com.fast.dev.ucenter.core.type.ServiceTokenType;
import com.fast.dev.ucenter.core.type.ServiceType;
import com.fast.dev.ucenter.core.type.TokenState;
import com.fast.dev.ucenter.core.type.UserLoginType;
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
    private UserCenterConf userCenterConfig;


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
        String code = createRobotValidate(robotValidate);


        //  创建业务令牌
        ServiceToken serviceToken = createServiceToken(tokenEnvironment, userLoginType.getLoginService(), loginName, code);
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
    public UserTokenModel login(String token, String validateCode, String passWord, long timeOut) {
        //取出业务令牌并验证码校验
        ServiceToken serviceToken = this.userTokenDao.query(token);
        TokenState tokenState = validateToken(serviceToken, validateCode);
        if (tokenState != null) {
            return new UserTokenModel(tokenState);
        }

        //判断业务令牌是否合法
        if (serviceToken.getServiceTokenType().getServiceType() != ServiceType.Login) {
            return new UserTokenModel(TokenState.TokenNotMatch);
        }

        //删除登陆令牌
        this.userTokenDao.remove(token);

        //根据登陆方式取用户信息
        BaseUser baseUser = findBaseUser(serviceToken);
        if (baseUser == null) {
            return new UserTokenModel(TokenState.UserNotExist);
        }

        //密码校验
        if (!PassWordUtil.validate(baseUser.getSalt(), passWord, baseUser.getPassWord())) {
            return new UserTokenModel(TokenState.PassWordError);
        }



        //创建用户令牌（入库并返回令牌对象）
        return createUserToken(baseUser, timeOut);
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
        String code = createRobotValidate(robotValidate);


        //创建业务令牌
        ServiceToken serviceToken = createServiceToken(loginEnvironment, userLoginType.getRegisterService(), loginName, code);
        if (serviceToken == null) {
            return new UserRegisterToken(TokenState.CreateError);
        }

        UserRegisterToken userRegisterToken = new UserRegisterToken(TokenState.Success);
        userRegisterToken.setRobotValidate(robotValidate);
        userRegisterToken.setToken(serviceToken.getToken());
        return userRegisterToken;
    }

    @Override
    public TokenState register(String token, String validateCode, String passWord) {
        //校验业务令牌的验证码
        ServiceToken serviceToken = this.userTokenDao.query(token);
        TokenState tokenState = validateToken(serviceToken, validateCode);
        if (tokenState != null) {
            return tokenState;
        }

        //业务令牌类型匹配判断
        if (serviceToken.getServiceTokenType().getServiceType() != ServiceType.Register) {
            return TokenState.TokenNotMatch;
        }

        //删除这个业务令牌
        this.userTokenDao.remove(serviceToken.getToken());

        //  实例化对象
        BaseUser baseUser = newBaseUser(serviceToken, passWord);
        if (baseUser == null) {
            return TokenState.Error;
        }

        //注册新用户入库
        createRegisterUser(baseUser);


        return baseUser.getId() == null ? TokenState.Error : TokenState.Success;
    }


    /**
     * 校验令牌
     *
     * @param serviceToken
     * @param validateCode
     * @return
     */
    private TokenState validateToken(ServiceToken serviceToken, String validateCode) {
        if (serviceToken == null) {
            return TokenState.TokenNotExist;
        }
        if (serviceToken.getAccessCount() > this.userCenterConfig.getMaxCanAccessCount()) {
            return TokenState.TokenMaxLimit;
        }
        if (!serviceToken.getValidateCode().equals(validateCode)) {
            return TokenState.ValidateCodeError;
        }
        return null;
    }


    /**
     * 创建业务令牌
     *
     * @return
     */
    private ServiceToken createServiceToken(TokenEnvironment loginEnvironment, ServiceTokenType serviceTokenType, String loginName, String code) {
        ServiceToken serviceToken = new ServiceToken();
        this.dbHelper.saveTime(serviceToken);
        serviceToken.setId(RandomUtil.uuid());
        serviceToken.setToken(TokenUtil.create());
        serviceToken.setServiceTokenType(serviceTokenType);
        serviceToken.setValidateCode(code);
        serviceToken.setAccessCount(0);
        serviceToken.setLoginName(loginName);
        if (this.userTokenDao.createServiceToken(serviceToken, this.userCenterConfig.getServiceTokenTimeOut())) {
            return serviceToken;
        }
        return null;
    }


    @Override
    public boolean ping(String uToken) {
        UserToken userToken = userTokenDao.queryOnly(uToken);
        return userToken != null;
    }
}
