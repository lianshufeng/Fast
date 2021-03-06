package com.fast.dev.ucenter.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.ucenter.core.model.UserDisableModel;
import com.fast.dev.ucenter.core.type.PassWordEncodeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 * <p>
 * 用户基础表
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document
public class BaseUser extends SuperEntity {

    /**
     * 用户名
     */
    @Indexed(unique = true, sparse = true)
    private String userName;

    /**
     * 身份证
     */
    @Indexed(unique = true, sparse = true)
    private String idCard;

    /**
     * 邮箱
     */
    @Indexed(unique = true, sparse = true)
    private String mail;

    /**
     * 电话号码
     */
    @Indexed(unique = true, sparse = true)
    private String phone;


    /**
     * 盐值
     */
    @Indexed
    private String salt;

    /**
     * 密码
     */
    @Indexed
    private String passWord;

    /**
     * 密码加密类型
     */
    private PassWordEncodeType passWordEncodeType;

    /**
     * 禁用期限
     */
    private UserDisableModel userDisable;




}
