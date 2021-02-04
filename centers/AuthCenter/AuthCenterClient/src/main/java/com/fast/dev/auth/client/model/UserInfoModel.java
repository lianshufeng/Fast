package com.fast.dev.auth.client.model;


import lombok.Getter;
import lombok.Setter;

/**
 * 用户基本信息常量
 */

public class UserInfoModel {

    public final static String Phone = "phone";
    public final static String RealName = "realName";
    public final static String NickName = "nickName";
    public final static String FaceUrl = "faceUrl";
    public final static String Sex = "sex";
    public final static String Birthday = "birthday";

    //手机号码
    @Setter
    @Getter
    private String phone;

    //真实姓名
    @Setter
    @Getter
    private String realName;

    //昵称
    @Setter
    @Getter
    private String nickName;

    //用户头像
    @Setter
    @Getter
    private String faceUrl;

    //性别
    @Setter
    @Getter
    private String sex;

    //生日
    @Setter
    @Getter
    private Long birthday;

}
