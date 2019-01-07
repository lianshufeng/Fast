package com.fast.dev.ucenter.core.service;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pushcenter.manager.helper.SendPushMessageHelper;
import com.fast.dev.pushcenter.manager.model.PlatformMessage;
import com.fast.dev.pushcenter.manager.type.MessageType;
import com.fast.dev.ucenter.core.conf.ValidateDataConf;
import com.fast.dev.ucenter.core.dao.BaseUserDao;
import com.fast.dev.ucenter.core.dao.UserTokenDao;
import com.fast.dev.ucenter.core.domain.*;
import com.fast.dev.ucenter.core.helper.ImageValidateHelper;
import com.fast.dev.ucenter.core.helper.UserPushMessageHelper;
import com.fast.dev.ucenter.core.helper.ValidateDataHelper;
import com.fast.dev.ucenter.core.helper.password.PassWordHelper;
import com.fast.dev.ucenter.core.model.*;
import com.fast.dev.ucenter.core.type.*;
import com.fast.dev.ucenter.core.util.BaseTokenUtil;
import com.fast.dev.ucenter.core.util.RandomUtil;
import com.fast.dev.ucenter.core.util.TokenUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Array;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 通用用户的业务
 */
public abstract class BaseUserService {

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


    @Autowired
    private UserService userService;

    @Autowired
    private PassWordHelper passWordHelper;


    //是否已做过强化校验
    private ThreadLocal<Boolean> hasStrongValidate = new ThreadLocal<>();


    /**
     * 用户注销
     *
     * @param token
     * @return
     */
    public boolean logout(String token) {
        return logout(token, false);
    }

    /**
     * 用户注销
     *
     * @param token
     * @return
     */
    public boolean logout(String token, boolean all) {
        BaseToken baseToken = this.userTokenDao.queryOnly(token);
        if (baseToken == null) {
            return false;
        }
        //待删除列表
        Set<BaseToken> baseTokenSet = new HashSet<>();
        baseTokenSet.add(baseToken);
        if (all) {
            Set<BaseToken> baseTokens = this.userTokenDao.findByUid(baseToken.getId());
            if (baseTokenSet != null) {
                baseTokenSet.clear();
                baseTokenSet.addAll(baseTokens);
            }
        }
        //删除
        for (BaseToken bt : baseTokenSet) {
            removeUserToken(bt);
        }
        return baseTokenSet.size() > 0;
    }

    /**
     * 删除指定的用户令牌
     *
     * @param baseToken
     */
    private boolean removeUserToken(BaseToken baseToken) {
        if (baseToken == null || !(baseToken instanceof UserToken)) {
            return false;
        }
        UserToken userToken = (UserToken) baseToken;
        boolean flag = this.userTokenDao.remove(userToken.getToken());
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
            if (!passWordHelper.validate(baseUser.getSalt(), oldPassWord, baseUser.getPassWord(), baseUser.getPassWordEncodeType())) {
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
    public void updateBaseUserPassWord(BaseUser baseUser, String passWord) {
        // 设置密码
        baseUser.setSalt(RandomUtil.uuid(6));
        baseUser.setPassWord(passWordHelper.enCode(baseUser.getSalt(), passWord, PassWordEncodeType.Default));
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
    protected String createRobotValidate(TokenEnvironment tokenEnvironment, RobotValidate robotValidate, ServiceTokenType serviceTokenType, String loginName) {

        //取出当前适合的配置信息
        ValidateDataConf validateDataConf = validateDataHelper.get(tokenEnvironment.getApp());

        // 校验数据
        ValidateData validateData = validateDataConf.getRule().get(robotValidate.getType());
        if (validateData == null) {
            validateData = new ValidateData();
        }
        //生成对应的验证码
        String code = ValidateDataHelper.getValidateRandomValue(validateData);

        //调试模式
        if (validateDataConf.isDebug()) {
            robotValidate.setType(ValidateType.Debug);
            robotValidate.setData(code);
        }
        //是否需要加强验证
        else if (validateData.isStrongValidate() && this.hasStrongValidate.get() == null) {
            robotValidate.setType(ValidateType.Strong);
            robotValidate.setData("data:image/png;base64," + Base64.getEncoder().encodeToString(this.imageValidateHelper.create(code)));
        }
        //手机验证码
        else if (robotValidate.getType() == ValidateType.Sms) {
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

        //doto 发送邮件或短信
        if (robotValidate.getType() == ValidateType.Sms || robotValidate.getType() == ValidateType.Mail) {
            String templateId = ValidateDataHelper.getTemplate(validateData, serviceTokenType.getServiceType());
            MessageType messageType = MessageType.valueOf(robotValidate.getType().name());
            pushToUserValidateCode(messageType, templateId, loginName, code);
        }


        //取消当前线程标记的强化校验
        this.hasStrongValidate.remove();

        return code;
    }


    /**
     * 推送到用户验证码
     */
    protected void pushToUserValidateCode(MessageType type, String templateId, String number, String code) {
        PlatformMessage message = new PlatformMessage();
        message.setContent(new HashMap<String, Object>() {{
            put("code", code);
        }});
        //设置模版id
        message.setTemplateId(templateId);
        message.setNumber(new String[]{number});
        //通过短信模版类型映射消息类型
        message.setMessageType(type);
        this.sendPushMessageHelper.pushPlatformMessage(message);
    }


    /**
     * 创建业务令牌
     *
     * @return
     */
    protected ServiceToken createServiceToken(TokenEnvironment tokenEnvironment, ServiceTokenType serviceTokenType, String loginName, String code, RobotValidate robotValidate) {
        //取出对应的配置
        ValidateDataConf validateDataConf = this.validateDataHelper.get(tokenEnvironment.getApp());
        //判断是否强化类型的令牌
        if (robotValidate.getType() == ValidateType.Strong) {
            StrongServiceToken strongServiceToken = new StrongServiceToken();
            setServiceToken(tokenEnvironment, serviceTokenType, loginName, strongServiceToken, code);
            if (this.userTokenDao.createStrongServiceToken(strongServiceToken, validateDataConf.getServiceTokenTimeOut())) {
                return strongServiceToken;
            }
        } else {
            ServiceToken serviceToken = new ServiceToken();
            setServiceToken(tokenEnvironment, serviceTokenType, loginName, serviceToken, code);
            if (this.userTokenDao.createServiceToken(serviceToken, validateDataConf.getServiceTokenTimeOut())) {
                return serviceToken;
            }
        }
        return null;
    }


    /**
     * 业务令牌
     *
     * @param tokenEnvironment
     * @param serviceTokenType
     * @param loginName
     * @param serviceToken
     */
    private void setServiceToken(TokenEnvironment tokenEnvironment, ServiceTokenType serviceTokenType, String loginName, ServiceToken serviceToken, String code) {
        this.dbHelper.saveTime(serviceToken);
        serviceToken.setToken(TokenUtil.create());
        serviceToken.setId(RandomUtil.uuid());
        serviceToken.setServiceTokenType(serviceTokenType);
        serviceToken.setAccessCount(0);
        serviceToken.setLoginName(loginName);
        serviceToken.setCreateTokenEnvironment(tokenEnvironment);
        serviceToken.setValidateCode(code);
    }


    /**
     * 校验令牌
     *
     * @param serviceToken
     * @param validateCode
     * @return
     */
    protected TokenState validateToken(ServiceToken serviceToken, String validateCode, boolean isValidateServiceToken) {
        if (serviceToken == null) {
            return TokenState.TokenNotExist;
        }
        //通过应用名取出对应的的配置
        ValidateDataConf validateDataConf = this.validateDataHelper.get(serviceToken.getCreateTokenEnvironment().getApp());
        if (serviceToken.getAccessCount() > validateDataConf.getMaxCanAccessCount()) {
            return TokenState.TokenMaxLimit;
        }
        if (!serviceToken.getValidateCode().equalsIgnoreCase(validateCode)) {
            return TokenState.ValidateCodeError;
        }
        //校验是否必须为业务令牌而非子类
        if (isValidateServiceToken && serviceToken.getClass() != ServiceToken.class) {
            return TokenState.TokenNotMatch;
        }
        return null;
    }


    /**
     * 加强令牌校验
     *
     * @param token
     * @param code
     * @return
     */
    public BasicServiceToken strongToken(String token, String code) {
        ServiceToken serviceToken = this.userTokenDao.query(token);
        //令牌验证错误
        TokenState tokenState = validateToken(serviceToken, code, false);
        if (tokenState != null) {
            return new StrongServiceTokenModel(tokenState);
        }

        //判断业务令牌是否合法
        if (!(serviceToken instanceof StrongServiceToken)) {
            return new StrongServiceTokenModel(TokenState.TokenNotMatch);
        }

        //确认当前线程已强化校验过
        this.hasStrongValidate.set(true);

        //创建时的环境而并非现在接收到的环境
        TokenEnvironment tokenEnvironment = serviceToken.getCreateTokenEnvironment();
        //业务令牌类型
        ServiceTokenType serviceTokenType = serviceToken.getServiceTokenType();
        //登录名
        String loginName = serviceToken.getLoginName();
        BasicServiceToken basicServiceToken = null;

        //调回原有接口
        if (serviceTokenType == ServiceTokenType.FastLogin) {
            basicServiceToken = this.userService.getFastToken(serviceToken.getLoginName(), tokenEnvironment);
        } else if (serviceTokenType.getServiceType() == ServiceType.Login) {
            basicServiceToken = this.userService.getUserLoginToken(UserLoginType.valueOf(serviceTokenType.getLoginType()), loginName, tokenEnvironment);
        } else if (serviceTokenType.getServiceType() == ServiceType.Register) {
            basicServiceToken = this.userService.getUserRegisterToken(UserLoginType.valueOf(serviceTokenType.getLoginType()), loginName, tokenEnvironment);
        } else if (serviceTokenType.getServiceType() == ServiceType.UpdatePassWord) {
            basicServiceToken = this.userService.getUpdatePassWordToken(UserLoginType.valueOf(serviceTokenType.getLoginType()), loginName, tokenEnvironment);
        }


        //删除该令牌
        if (serviceTokenType != null) {
            this.userTokenDao.remove(token);
        }

        return basicServiceToken;
    }


    /**
     * 设置基础用户类型所对应的值
     *
     * @param baseUser
     * @param userLoginType
     * @param loginName
     */
    protected void setBaseUserLoginName(BaseUser baseUser, UserLoginType userLoginType, String loginName) {
        if (userLoginType == UserLoginType.Phone) {
            baseUser.setPhone(loginName);
        } else if (userLoginType == UserLoginType.UserName) {
            baseUser.setUserName(loginName);
        }
    }


    /**
     * 清空用户的用户令牌
     *
     * @param uid
     * @return
     */
    public long cleanUserToken(String uid,String [] ignoreUToken) {
        Set<BaseToken> baseTokens = this.userTokenDao.findByUid(uid);
        if (baseTokens == null || baseTokens.size() == 0) {
            return 0;
        }
        for (BaseToken baseToken : baseTokens) {
            if (ignoreUToken!=null && !ArrayUtils.contains(ignoreUToken,baseToken.getToken())){
                removeUserToken(baseToken);
            }
        }
        return baseTokens.size();
    }


}
