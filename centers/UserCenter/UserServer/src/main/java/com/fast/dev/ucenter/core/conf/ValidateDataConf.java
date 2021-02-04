package com.fast.dev.ucenter.core.conf;

import com.fast.dev.ucenter.core.model.ValidateData;
import com.fast.dev.ucenter.core.type.ValidateCodeScope;
import com.fast.dev.ucenter.core.type.ValidateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：练书锋
 * 时间：2018/8/30
 * 验证码配置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "ucenter.validate")
public class ValidateDataConf implements Serializable {

    /**
     * 是否调试模式
     */
    private boolean debug;


    /**
     * 业务令牌超时时间
     */
    private long serviceTokenTimeOut = 5 * 60 * 1000L;


    /**
     * 最大的访问次数
     */
    private int maxCanAccessCount = 5;


    /**
     * 验证码生成规则
     */
    private Map<ValidateType, ValidateData> rule = new HashMap<ValidateType, ValidateData>() {{
        put(ValidateType.Sms, new ValidateData(ValidateCodeScope.Number, 4, null, null, false));
        put(ValidateType.Image, new ValidateData(ValidateCodeScope.Charset, 4, null, null, false));
        put(ValidateType.Mail, new ValidateData(ValidateCodeScope.Charset, 4, null, null, false));
    }};


    /**
     * 其他应用配置
     */
    private Map<String, ValidateDataConf> app;

}
