package com.fast.dev.ucenter.core.domain;

import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.type.ServiceTokenType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.springframework.data.mongodb.core.index.Indexed;

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
    @Indexed
    private ServiceTokenType serviceTokenType;


    /**
     * 校验码
     */
    @Indexed
    private String validateCode;


    /**
     * 登陆名
     */
    @Indexed
    private String loginName;

}
