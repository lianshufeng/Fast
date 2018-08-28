package com.fast.dev.ucenter.core.domain;

import com.fast.dev.ucenter.core.type.ServiceTokenType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.asn1.dvcs.ServiceType;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 * 用户令牌
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceToken extends BaseToken {


    /**
     * 业务类型
     */
    private ServiceTokenType serviceTokenType;


    /**
     * 校验码
     */
    private String validateCode;


    /**
     * 登陆名
     */
    private String loginName;


}
