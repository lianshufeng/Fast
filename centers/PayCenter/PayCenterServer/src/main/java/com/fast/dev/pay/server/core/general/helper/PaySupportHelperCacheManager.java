package com.fast.dev.pay.server.core.general.helper;

import com.fast.dev.core.util.encode.HashUtil;
import com.fast.dev.pay.client.type.AccountType;
import com.fast.dev.pay.server.core.exception.ExceptionEvent;
import com.fast.dev.pay.server.core.exception.PostException;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;
import com.fast.dev.pay.server.core.general.helper.ali.AliPayHelper;
import com.fast.dev.pay.server.core.general.helper.weixin.WeiXinHelper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Slf4j
@Component
public class PaySupportHelperCacheManager implements ApplicationListener<ExceptionEvent> {

    //缓存名
    private final static String CacheName = "PaySupportCache";

    //缓存
    private Cache<String, PaySupportHelper> cache;

    //当前线程，存缓存
    private ThreadLocal<String> accountThreadLocal = new ThreadLocal<>();

    @Autowired
    private ApplicationContext applicationContext;


    //类型匹配到支付类型的助手
    private final static Map<AccountType, Class<? extends PaySupportHelper>> AccountTypeMap = Map.of(
            AccountType.WeiXinPay, WeiXinHelper.class,
            AccountType.AliPay, AliPayHelper.class
    );


    @Autowired
    private void init(CacheManager cacheManager) {
        this.cache = (Cache) cacheManager.getCache(CacheName).getNativeCache();
    }


    /**
     * 查询缓存助手
     *
     * @param enterprisePayAccount
     * @param accountType
     * @param <T>
     * @return
     */
    public <T extends PaySupportHelper> T get(EnterprisePayAccount enterprisePayAccount, AccountType accountType) {
        return get(enterprisePayAccount, AccountTypeMap.get(accountType));
    }


    /**
     * 查询缓存助手
     *
     * @return
     */
    public <T extends PaySupportHelper> T get(EnterprisePayAccount enterprisePayAccount, Class<? extends PaySupportHelper> cls) {
        //缓存的key
        final String cacheKey = enterprisePayAccount.getId();
        this.accountThreadLocal.set(cacheKey);

        PaySupportHelper paySupportHelper = this.cache.getIfPresent(cacheKey);
        if (paySupportHelper == null) {
            paySupportHelper = this.applicationContext.getBean(cls);
            paySupportHelper.setEnterprisePayAccount(enterprisePayAccount);
            paySupportHelper.after();
            this.cache.put(cacheKey, paySupportHelper);
        } else {
            log.info("三方支付,命中缓存 : " + cacheKey);
        }
        return (T) paySupportHelper;
    }


    /**
     * 删除缓存
     */
    private void removeCacheFromException() {
        final String cacheKey = this.accountThreadLocal.get();
        if (cacheKey == null) {
            return;
        }
        this.cache.asMap().remove(cacheKey);
        log.info("三方支付,删除缓存 : {}", cacheKey);
    }


    @Override
    public void onApplicationEvent(ExceptionEvent exceptionEvent) {
        removeCacheFromException();
    }
}



