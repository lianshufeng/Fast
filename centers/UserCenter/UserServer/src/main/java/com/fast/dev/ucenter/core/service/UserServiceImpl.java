package com.fast.dev.ucenter.core.service;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.ucenter.core.conf.UserCenterConf;
import com.fast.dev.ucenter.core.dao.UserBaseDao;
import com.fast.dev.ucenter.core.dao.UserTokenDao;
import com.fast.dev.ucenter.core.domain.ServiceToken;
import com.fast.dev.ucenter.core.model.*;
import com.fast.dev.ucenter.core.type.ServiceTokenType;
import com.fast.dev.ucenter.core.type.TokenState;
import com.fast.dev.ucenter.core.type.UserLoginType;
import com.fast.dev.ucenter.core.type.ValidateType;
import com.fast.dev.ucenter.core.helper.RandomUtil;
import com.fast.dev.ucenter.core.helper.ValidataCodeUtil;
import com.fast.dev.ucenter.core.helper.ImageValidataHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserBaseDao userBaseDao;


    @Autowired
    private UserTokenDao userTokenDao;


    @Autowired
    private DBHelper dbHelper;


    @Autowired
    private UserCenterConf userCenterConfig;


    @Autowired
    private ImageValidataHelper imageValidataHelper;


    @Override
    public UserLoginToken getUserLoginToken(UserLoginType userLoginType, String loginName, LoginEnvironment loginEnvironment) {
        return null;
    }

    @Override
    public UserToken login(String token, String validateCode, String passWord) {
        return null;
    }

    @Override
    public TokenState logout(String token) {
        return null;
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
    public TokenState register(String token, String code, String passWord) {
        ServiceToken serviceToken = this.userTokenDao.query(token);
        if (serviceToken == null) {

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
        if (this.userBaseDao.existsByPhone(loginName)) {
            return new UserRegisterToken(TokenState.UserExist);
        }
        //手机验证的生成规则
        //创建机器校验码
        RobotValidate robotValidate = new RobotValidate(ValidateType.Phone);
        return getRegisterToken(loginEnvironment, loginName, robotValidate);
    }


    /**
     * 获取用户名注册令牌
     *
     * @param loginEnvironment
     * @param loginName
     * @return
     */
    public UserRegisterToken getUserNameRegisterToken(LoginEnvironment loginEnvironment, String loginName) {
        if (this.userBaseDao.existsByUserName(loginName)) {
            return new UserRegisterToken(TokenState.UserExist);
        }
        //创建机器校验码
        RobotValidate robotValidate = new RobotValidate(ValidateType.Image);
        return getRegisterToken(loginEnvironment, loginName, robotValidate);
    }


    /**
     * 获取注册令牌
     *
     * @param loginEnvironment
     * @param loginName
     * @param robotValidate
     * @return
     */
    private UserRegisterToken getRegisterToken(LoginEnvironment loginEnvironment, String loginName, RobotValidate robotValidate) {
        String code = createRobotValidate(robotValidate);
        //入库
        ServiceToken serviceToken = createServiceToken(loginEnvironment, ServiceTokenType.Register, loginName, code, 5 * 60 * 1000L);
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
            code = ValidataCodeUtil.createOnlyNumber(userCenterConfig.getPhoneValidataLength());
            //手机调试模式
            if (this.userCenterConfig.isPhoneValidataDebug()) {
                robotValidate.setType(ValidateType.PhoneDebug);
                robotValidate.setData(code);
            }
        }

        // 图形验证码
        else if (robotValidate.getType() == ValidateType.Image) {
            code = ValidataCodeUtil.create(userCenterConfig.getImageValidataLength());
            String data = "data:image/png;base64," + Base64.getEncoder().encodeToString(this.imageValidataHelper.create(code));
            robotValidate.setData(data);
        }


        return code;
    }

    /**
     * 创建业务令牌
     *
     * @return
     */
    private ServiceToken createServiceToken(LoginEnvironment loginEnvironment, ServiceTokenType serviceTokenType, String loginName, String code, long timeOut) {
        ServiceToken serviceToken = new ServiceToken();
        serviceToken.setId(RandomUtil.uuid());
        serviceToken.setCreateTime(dbHelper.getTime());
        serviceToken.setUpdateTime(dbHelper.getTime());
        serviceToken.setToken(RandomUtil.uuid());
        serviceToken.setServiceTokenType(serviceTokenType);
        serviceToken.setValidataCode(code);
        serviceToken.setAccessCount(0);
        if (this.userTokenDao.createServiceToken(serviceToken, loginName, timeOut)) {
            return serviceToken;
        }
        return null;
    }


}
