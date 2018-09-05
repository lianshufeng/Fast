package com.fast.dev.ucenter.core.type;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 * 业务令牌类型
 */
public enum ServiceTokenType {

    /**
     * 快捷登陆
     */
    FastLogin("快捷登陆", "Phone", ServiceType.Fast),

    /**
     * 手机注册
     */
    PhoneRegister("手机注册", "Phone", ServiceType.Register),
    /**
     * 手机登录
     */
    PhoneLogin("手机登陆", "Phone", ServiceType.Login),

    /**
     * 手机修改密码
     */
    PhoneUpdatePassWord("手机修改密码", "Phone", ServiceType.UpdatePassWord),


    /**
     * 用户名登陆
     */
    UserNameLogin("用户名登陆", "UserName", ServiceType.Login),

    /**
     * 用户名注册
     */
    UserNameRegister("用户名注册", "UserName", ServiceType.Register),

    /**
     * 用户修改密码
     */
    UserNameUpdatePassWord("用户修改密码", "UserName", ServiceType.UpdatePassWord),


    /**
     * 邮箱登陆
     */
    MailLogin("邮箱登陆", "Mail", ServiceType.Login),

    /**
     * 邮箱注册
     */
    MailRegister("邮箱注册", "Mail", ServiceType.Register),

    /**
     * 邮箱修改密码
     */
    MailUpdatePassWord("邮箱修改密码", "Mail", ServiceType.UpdatePassWord),


    /**
     * 身份证登陆
     */
    IdCardLogin("邮箱登陆", "IdCard", ServiceType.Login),

    /**
     * 身份证注册
     */
    IdCardRegister("邮箱注册", "IdCard", ServiceType.Register),


    ;

    /**
     * 备注
     */
    private String remark;

    /**
     * 登陆方式
     */
    private String loginType;

    /**
     * 业务类型
     */
    private ServiceType serviceType;

    ServiceTokenType(String remark, String loginType, ServiceType serviceType) {
        this.remark = remark;
        this.loginType = loginType;
        this.serviceType = serviceType;
    }

    public String getRemark() {
        return remark;
    }

    public String getLoginType() {
        return loginType;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }
}
