package com.fast.dev.auth.client.constant;

import com.fast.dev.auth.client.type.AuthConstantType;

public class ResourceAuthConstant {

    /**
     * 超级管理员
     */
    public final static String SuperAdmin = "SuperAdmin";

    /**
     * 管理员
     */
    public final static String Admin = "Admin";


    /**
     * 普通的登录用户权限
     */
    public final static String User = "User";


    /**
     * 内部接口管理
     */
    public final static String Manager = "Manager";


    /**
     * 权限枚举类型
     */
    public final static AuthConstantType[] authConstantTypes = AuthConstantType.values();

}
