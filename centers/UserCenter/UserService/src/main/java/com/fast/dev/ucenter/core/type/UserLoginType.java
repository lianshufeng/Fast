package com.fast.dev.ucenter.core.type;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 */
public enum UserLoginType {

    /**
     * 自定义用户登陆
     */
    UserName("用户名", ValidateType.Image, ServiceTokenType.UserNameRegister, ServiceTokenType.UserNameLogin, ServiceTokenType.UserNameUpdatePassWord, "userName"),

    /**
     * 手机登陆
     */
    Phone("手机", ValidateType.Sms, ServiceTokenType.PhoneRegister, ServiceTokenType.PhoneLogin, ServiceTokenType.PhoneUpdatePassWord, "phone"),

    /**
     * 邮箱
     */
    Mail("邮箱", ValidateType.Mail, ServiceTokenType.MailRegister, ServiceTokenType.MailLogin, ServiceTokenType.MailUpdatePassWord, "mail"),

    /**
     * 身份
     */
    IdCard("身份证", ValidateType.Image, ServiceTokenType.IdCardRegister, ServiceTokenType.IdCardLogin, null, "idCard"),


    /**
     * 微信登陆
     */
    WeiXin("微信", ValidateType.None, null, null, null, null),


    /**
     * QQ登陆
     */
    QQ("QQ", ValidateType.None, null, null, null, null);


    /**
     * 备注
     */
    private String remark;

    /**
     * 验证方式
     */
    private ValidateType validateType;

    /**
     * 注册业务
     */
    private ServiceTokenType registerService;

    /**
     * 登陆业务
     */
    private ServiceTokenType loginService;


    /**
     * 修改密码业务
     */
    private ServiceTokenType updatePassWordService;


    /**
     * 用户登陆名
     */
    private String userLoginTypeName;


    UserLoginType(String remark, ValidateType validateType, ServiceTokenType registerService, ServiceTokenType loginService, ServiceTokenType updatePassWordService, String userLoginTypeName) {
        this.remark = remark;
        this.validateType = validateType;
        this.registerService = registerService;
        this.loginService = loginService;
        this.userLoginTypeName = userLoginTypeName;
        this.updatePassWordService = updatePassWordService;
    }

    /**
     * 取出校验类型
     *
     * @return
     */
    public ValidateType getValidateType() {
        return validateType;
    }

    /**
     * 取出注册的业务类型
     *
     * @return
     */
    public ServiceTokenType getRegisterService() {
        return registerService;
    }

    /**
     * 取出登陆的业务类型
     *
     * @return
     */
    public ServiceTokenType getLoginService() {
        return loginService;
    }

    /**
     * 获取用户登陆类型的字段名称
     *
     * @return
     */
    public String getUserLoginTypeName() {
        return userLoginTypeName;
    }

    public ServiceTokenType getUpdatePassWordService() {
        return updatePassWordService;
    }
}
