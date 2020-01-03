package com.fast.dev.robot.robotserver.core.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class ValidatePhoneTokenRet {


    /**
     * 校验令牌
     */
    private String token;


    /**
     * 创建时间
     */
    private long createTime;


}
