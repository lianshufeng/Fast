package com.fast.dev.robot.robotserver.core.domain;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PhoneCodeRecord extends TokenRecord {

    /**
     * 手机号码
     */
    @Indexed
    private String phone;

    /**
     * 验证码
     */
    @Indexed
    private String code;


    /**
     * 尝试次数
     */
    @Indexed
    private int tryCount;



}
