package com.fast.dev.pay.server.core.general.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "bind-card")
public class UserBindBankCardConf {

    //用户绑定银行卡超时时间,单位分支
    private int bindCardTimeOutMinute = 8;


}
