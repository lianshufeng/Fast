package com.fast.dev.ucenter.core.service;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.ucenter.core.conf.UserCenterConf;
import com.fast.dev.ucenter.core.dao.BaseUserDao;
import com.fast.dev.ucenter.core.dao.UserTokenDao;
import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.domain.ServiceToken;
import com.fast.dev.ucenter.core.domain.UserToken;
import com.fast.dev.ucenter.core.helper.ImageValidateHelper;
import com.fast.dev.ucenter.core.model.*;
import com.fast.dev.ucenter.core.type.ServiceTokenType;
import com.fast.dev.ucenter.core.type.TokenState;
import com.fast.dev.ucenter.core.type.UserLoginType;
import com.fast.dev.ucenter.core.type.ValidateType;
import com.fast.dev.ucenter.core.util.PassWordUtil;
import com.fast.dev.ucenter.core.util.RandomUtil;
import com.fast.dev.ucenter.core.util.TokenUtil;
import com.fast.dev.ucenter.core.util.ValidateCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

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


    @Autowired
    private ImageValidateHelper imageValidateHelper;


    @Override
    public UserLoginToken getUserLoginToken(UserLoginType userLoginType, String loginName, LoginEnvironment loginEnvironment) {
        ServiceTokenType serviceTokenType = null;
        if (userLoginType == UserLoginType.Phone) {
            serviceTokenType = ServiceTokenType.PhoneLogin;
            if (!baseUserDao.existsByPhone(loginName)) {
                return new UserLoginToken(TokenState.UserNotExist);
            }
        } else if (userLoginType == UserLoginType.UserName) {
            serviceTokenType = ServiceTokenType.UserNameLogin;
            if (!baseUserDao.existsByUserName(loginName)) {
                return new UserLoginToken(TokenState.UserNotExist);
            }
        }
        // 机器校验
        RobotValidate robotValidate = new RobotValidate();
        robotValidate.setType(ValidateType.Image);
        String code = createRobotValidate(robotValidate);


        //  创建业务令牌
        ServiceToken serviceToken = createServiceToken(loginEnvironment, serviceTokenType, loginName, code);
        if (serviceToken == null) {
            return new UserLoginToken(TokenState.CreateError);
        }

        //返回用户登陆令牌
        UserLoginToken userLoginToken = new UserLoginToken();
        userLoginToken.setRobotValidate(robotValidate);
        userLoginToken.setTokenState(TokenState.Success);
        userLoginToken.setToken(serviceToken.getToken());
        return userLoginToken;
    }


    @Override
    public UserTokenModel login(String token, String validateCode, String passWord, long timeOut) {
        //取出业务令牌并校验令牌的合法性
        ServiceToken serviceToken = this.userTokenDao.query(token);
        TokenState tokenState = validateToken(serviceToken, validateCode);
        if (tokenState != null) {
            return new UserTokenModel(tokenState);
        }
        if (serviceToken.getServiceTokenType() != ServiceTokenType.UserNameLogin && serviceToken.getServiceTokenType() != ServiceTokenType.PhoneLogin) {
            return new UserTokenModel(TokenState.TokenNotMatch);
        }


        //根据登陆方式取用户信息
        BaseUser baseUser = null;
        if (serviceToken.getServiceTokenType() == ServiceTokenType.PhoneLogin) {
            baseUser = baseUserDao.findTop1ByPhone(serviceToken.getLoginName());
        } else if (serviceToken.getServiceTokenType() == ServiceTokenType.UserNameLogin) {
            baseUser = baseUserDao.findTop1ByUserName(serviceToken.getLoginName());
        }
        if (baseUser == null) {
            return new UserTokenModel(TokenState.UserNotExist);
        }

        //密码校验
        if (!PassWordUtil.validate(baseUser.getSalt(), passWord, baseUser.getPassWord())) {
            return new UserTokenModel(TokenState.PassWordError);
        }
        //删除登陆令牌
        userTokenDao.remove(token);
        //创建用户令牌（入库并返回令牌对象）
        return createUserToken(baseUser, timeOut);
    }


    @Override
    public UserRegisterToken getUserRegisterToken(UserLoginType userLoginType, String loginName, LoginEnvironment loginEnvironment) {
        if (userLoginType == UserLoginType.Phone) {
            return getPhoneRegisterToken(loginEnvironment, loginName);
        } else if (userLoginType == UserLoginType.UserName) {
            return getUserNameRegisterToken(loginEnvironment, loginName);
        }
        //不支持的注册方式
        return new UserRegisterToken(TokenState.NotSupportType);
    }

    @Override
    public TokenState register(String token, String validateCode, String passWord) {
        ServiceToken serviceToken = this.userTokenDao.query(token);
        TokenState tokenState = validateToken(serviceToken, validateCode);
        if (tokenState != null) {
            return tokenState;
        }
        if (serviceToken.getServiceTokenType() != ServiceTokenType.PhoneRegister && serviceToken.getServiceTokenType() != ServiceTokenType.UserNameRegister) {
            return TokenState.TokenNotMatch;
        }


        //  入库
        BaseUser baseUser = new BaseUser();
        if (serviceToken.getServiceTokenType() == ServiceTokenType.PhoneRegister) {
            baseUser.setPhone(serviceToken.getLoginName());
        } else if (serviceToken.getServiceTokenType() == ServiceTokenType.UserNameRegister) {
            baseUser.setUserName(serviceToken.getLoginName());
        }

        // 设置密码
        baseUser.setSalt(RandomUtil.uuid(6));
        baseUser.setPassWord(PassWordUtil.enCode(baseUser.getSalt(), passWord));

        //注册新用户入库
        createRegisterUser(baseUser);

        //删除这个令牌
        if (baseUser.getId() != null) {
            this.userTokenDao.remove(serviceToken.getToken());
        }

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
     * 获取收注册令牌
     *
     * @param loginEnvironment
     * @param loginName
     * @return
     */
    public UserRegisterToken getPhoneRegisterToken(LoginEnvironment loginEnvironment, String loginName) {
        if (baseUserDao.existsByPhone(loginName)) {
            return new UserRegisterToken(TokenState.UserExist);
        }
        //手机验证的生成规则
        //创建机器校验码
        RobotValidate robotValidate = new RobotValidate(ValidateType.Phone);
        return getRegisterToken(loginEnvironment, loginName, ServiceTokenType.PhoneRegister, robotValidate);
    }


    /**
     * 获取用户名注册令牌
     *
     * @param loginEnvironment
     * @param loginName
     * @return
     */
    public UserRegisterToken getUserNameRegisterToken(LoginEnvironment loginEnvironment, String loginName) {
        if (baseUserDao.existsByUserName(loginName)) {
            return new UserRegisterToken(TokenState.UserExist);
        }
        //创建机器校验码
        RobotValidate robotValidate = new RobotValidate(ValidateType.Image);
        return getRegisterToken(loginEnvironment, loginName, ServiceTokenType.UserNameRegister, robotValidate);
    }


    /**
     * 获取注册令牌
     *
     * @param loginEnvironment
     * @param loginName
     * @param robotValidate
     * @return
     */
    private UserRegisterToken getRegisterToken(LoginEnvironment loginEnvironment, String loginName, ServiceTokenType serviceTokenType, RobotValidate robotValidate) {
        String code = createRobotValidate(robotValidate);
        //入库
        ServiceToken serviceToken = createServiceToken(loginEnvironment, serviceTokenType, loginName, code);
        if (serviceToken == null) {
            return new UserRegisterToken(TokenState.CreateError);
        }
        UserRegisterToken userRegisterToken = new UserRegisterToken();
        userRegisterToken.setRobotValidate(robotValidate);
        userRegisterToken.setToken(serviceToken.getToken());
        userRegisterToken.setTokenState(TokenState.Success);
        return userRegisterToken;
    }


    /**
     * 创建机器验证码
     *
     * @return 返回 验证值
     */
    private String createRobotValidate(RobotValidate robotValidate) {

        String code = null;
        if (robotValidate.getType() == ValidateType.Phone) {
            code = ValidateCodeUtil.createOnlyNumber(userCenterConfig.getPhoneValidateLength());
            // 发送短信
            //doto
        }

        // 图形验证码
        else if (robotValidate.getType() == ValidateType.Image) {
            code = ValidateCodeUtil.create(userCenterConfig.getPhoneValidateLength());
            String data = "data:image/png;base64," + Base64.getEncoder().encodeToString(this.imageValidateHelper.create(code));
            robotValidate.setData(data);
        }
        //调试
        if (this.userCenterConfig.isDebug()) {
            robotValidate.setType(ValidateType.Debug);
            robotValidate.setData(code);
        }

        return code;
    }

    /**
     * 创建业务令牌
     *
     * @return
     */
    private ServiceToken createServiceToken(LoginEnvironment loginEnvironment, ServiceTokenType serviceTokenType, String loginName, String code) {
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
        UserToken userToken = userTokenDao.query(uToken);
        return userToken != null;
    }
}
