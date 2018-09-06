package com.fast.dev.ucenter.core.service;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pushcenter.manager.helper.SendPushMessageHelper;
import com.fast.dev.pushcenter.manager.model.PlatformMessage;
import com.fast.dev.pushcenter.manager.type.MessageType;
import com.fast.dev.ucenter.core.conf.ValidateDataConf;
import com.fast.dev.ucenter.core.dao.BaseUserDao;
import com.fast.dev.ucenter.core.dao.UserTokenDao;
import com.fast.dev.ucenter.core.domain.BaseToken;
import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.domain.ServiceToken;
import com.fast.dev.ucenter.core.domain.UserToken;
import com.fast.dev.ucenter.core.helper.ImageValidateHelper;
import com.fast.dev.ucenter.core.helper.UserPushMessageHelper;
import com.fast.dev.ucenter.core.helper.ValidateDataHelper;
import com.fast.dev.ucenter.core.model.RobotValidate;
import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.core.model.ValidateData;
import com.fast.dev.ucenter.core.type.*;
import com.fast.dev.ucenter.core.util.BaseTokenUtil;
import com.fast.dev.ucenter.core.util.PassWordUtil;
import com.fast.dev.ucenter.core.util.RandomUtil;
import com.fast.dev.ucenter.core.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Base64;
import java.util.HashMap;

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


    @Autowired
    private ValidateDataHelper validateDataHelper;


    @Autowired
    private ImageValidateHelper imageValidateHelper;


    @Autowired
    private SendPushMessageHelper sendPushMessageHelper;


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
    public UserTokenModel createUserToken(TokenEnvironment env, BaseUser baseUser, long expireTime) {
        // 用户令牌并入库
        UserToken userToken = new UserToken();
        userToken.setId(RandomUtil.uuid());
        this.dbHelper.saveTime(userToken);
        userToken.setUid(baseUser.getId());
        userToken.setSecret(TokenUtil.create());
        userToken.setToken(TokenUtil.create());
        userToken.setCreateTokenEnvironment(env);
        UserTokenModel userTokenModel = null;
        if (this.userTokenDao.createUserToken(userToken, expireTime)) {
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


    /**
     * 用户是否存在
     *
     * @param userLoginType
     * @param loginName
     * @return
     */
    public boolean existsUser(UserLoginType userLoginType, String loginName) {
        if (userLoginType == UserLoginType.Phone) {
            return baseUserDao.existsByPhone(loginName);
        } else if (userLoginType == UserLoginType.UserName) {
            return baseUserDao.existsByUserName(loginName);
        }
        return false;
    }


    /**
     * 修改密码
     *
     * @param serviceToken
     * @param oldPassWord
     * @param newPassWord
     * @return
     */
    public TokenState updateUserPassWord(ServiceToken serviceToken, String oldPassWord, String newPassWord) {
        //账号不存在
        BaseUser baseUser = this.findBaseUser(serviceToken);
        if (baseUser == null || baseUser.getId() == null) {
            return TokenState.UserNotExist;
        }

        //如果不是邮件或者短信修改密码则需要验证原密码
        if (serviceToken.getServiceTokenType() != ServiceTokenType.MailUpdatePassWord && serviceToken.getServiceTokenType() != ServiceTokenType.PhoneUpdatePassWord) {
            if (!PassWordUtil.validate(baseUser.getSalt(), oldPassWord, baseUser.getPassWord())) {
                return TokenState.PassWordError;
            }
        }

        // 设置密码
        updateBaseUserPassWord(baseUser, newPassWord);
        //保存
        return this.baseUserDao.updatePassWord(baseUser.getId(), baseUser.getSalt(), baseUser.getPassWord()) ? TokenState.Success : TokenState.Error;
    }


    /**
     * 更新用户密码
     *
     * @param baseUser
     */
    private static void updateBaseUserPassWord(BaseUser baseUser, String passWord) {
        // 设置密码
        baseUser.setSalt(RandomUtil.uuid(6));
        baseUser.setPassWord(PassWordUtil.enCode(baseUser.getSalt(), passWord));
    }


    /**
     * @param serviceToken
     * @param passWord
     * @return
     */
    public BaseUser newBaseUser(ServiceToken serviceToken, String passWord) {
        BaseUser baseUser = new BaseUser();
        if (serviceToken.getServiceTokenType() == ServiceTokenType.PhoneRegister) {
            baseUser.setPhone(serviceToken.getLoginName());
        } else if (serviceToken.getServiceTokenType() == ServiceTokenType.UserNameRegister) {
            baseUser.setUserName(serviceToken.getLoginName());
        } else {
            return null;
        }
        // 设置密码
        updateBaseUserPassWord(baseUser, passWord);
        return baseUser;
    }

    /**
     * 查询用户
     *
     * @param serviceToken
     * @return
     */
    public BaseUser findBaseUser(ServiceToken serviceToken) {
        switch (serviceToken.getServiceTokenType().getLoginType()) {
            case "Phone":
                return this.baseUserDao.findTop1ByPhone(serviceToken.getLoginName());
            case "UserName":
                return this.baseUserDao.findTop1ByUserName(serviceToken.getLoginName());
            case "Mail":
                return this.baseUserDao.findTop1ByMail(serviceToken.getLoginName());
            case "IdCard":
                return this.baseUserDao.findTop1ByIdCard(serviceToken.getLoginName());
        }
        return null;
    }


    /**
     * 通过手机号码查找用户，若不存在则新建一个
     *
     * @return
     */
    public BaseUser findAndSaveBaseUser(ServiceToken serviceToken) {
        return this.baseUserDao.findAndSaveBaseUser(serviceToken.getLoginName());
    }


    /**
     * 创建机器验证码
     *
     * @return 返回 验证值
     */
    protected String createRobotValidate(TokenEnvironment tokenEnvironment, RobotValidate robotValidate, ServiceType serviceType, String loginName) {

        //取出当前适合的配置信息
        ValidateDataConf validateDataConf = validateDataHelper.get(tokenEnvironment.getApp());

        // 校验数据
        ValidateData validateData = validateDataConf.getRule().get(robotValidate.getType());
        if (validateData == null) {
            validateData = new ValidateData();
        }
        //生成对应的验证码
        String code = ValidateDataHelper.getValidateRandomValue(validateData);

        //手机验证码
        if (robotValidate.getType() == ValidateType.Sms) {
            robotValidate.setData(null);
        }
        //邮箱
        else if (robotValidate.getType() == ValidateType.Mail) {
            robotValidate.setData(null);
        }
        // 图形验证码
        else if (robotValidate.getType() == ValidateType.Image) {
            robotValidate.setData("data:image/png;base64," + Base64.getEncoder().encodeToString(this.imageValidateHelper.create(code)));
        }


        //调试
        if (validateDataConf.isDebug()) {
            robotValidate.setType(ValidateType.Debug);
            robotValidate.setData(code);
        } else {
            //doto 发送邮件或短信
            if (robotValidate.getType() == ValidateType.Sms || robotValidate.getType() == ValidateType.Mail) {
                PlatformMessage message = new PlatformMessage();
                message.setContent(new HashMap<String, Object>() {{
                    put("code", code);
                }});
                //设置模版id
                message.setTemplateId(ValidateDataHelper.getTemplate(validateData, serviceType));
                message.setNumber(new String[]{loginName});
                //通过短信模版类型映射消息类型
                message.setMessageType(MessageType.valueOf(robotValidate.getType().name()));
                this.sendPushMessageHelper.pushPlatformMessage(message);
            }
        }

        return code;
    }


}
