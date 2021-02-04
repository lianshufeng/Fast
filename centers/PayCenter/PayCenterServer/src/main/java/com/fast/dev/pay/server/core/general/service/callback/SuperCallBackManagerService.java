package com.fast.dev.pay.server.core.general.service.callback;

import com.fast.dev.pay.client.type.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SuperCallBackManagerService {

    private Map<AccountType, SuperCallBackService> map = new ConcurrentHashMap<>();

    @Autowired
    private void init(ApplicationContext applicationContext) {
        map.clear();
        for (SuperCallBackService backService : applicationContext.getBeansOfType(SuperCallBackService.class).values()) {
            map.put(backService.accountType(), backService);
        }
    }


    /**
     * 通过支付类型取出回调的业务
     *
     * @param accountType
     * @return
     */
    public SuperCallBackService get(AccountType accountType) {
        return map.get(accountType);
    }


}
