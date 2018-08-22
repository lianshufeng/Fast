package com.fast.dev.ucenter.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 *
 * 用户基础表
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document
public class UserBase extends SuperEntity
{

    /**
     * 用户名
     */
    @Indexed(unique = true)
    private String userName;

    /**
     * 身份证
     */
    @Indexed(unique = true)
    private String idCard;

    /**
     * 邮箱
     */
    @Indexed(unique = true)
    private String mail;

    /**
     * 电话号码
     */
    @Indexed(unique = true)
    private String phone;

}
