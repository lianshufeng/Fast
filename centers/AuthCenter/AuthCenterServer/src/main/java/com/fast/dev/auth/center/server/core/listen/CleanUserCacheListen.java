package com.fast.dev.auth.center.server.core.listen;

import com.fast.dev.auth.center.server.core.annotations.CleanUserCache;
import com.fast.dev.auth.center.server.core.event.CleanUserCacheEvent;
import com.fast.dev.auth.center.server.core.helper.CacheCleanHelper;
import com.fast.dev.auth.center.server.core.type.CleanUserCacheType;
import com.fast.dev.core.event.method.MethodEvent;
import com.fast.dev.core.util.spring.SpringELUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CleanUserCacheListen implements ApplicationListener<CleanUserCacheEvent> {

    @Autowired
    private CacheCleanHelper cacheCleanHelper;


    @Override
    public void onApplicationEvent(CleanUserCacheEvent cleanUserCacheEvent) {
        MethodEvent.Source source = cleanUserCacheEvent.getSource();
        if (source.getCallType() == MethodEvent.CallType.After) {
            cleanUserCache(source);
        }
    }

    /**
     * 清空用户的缓存信息
     */
    private void cleanUserCache(MethodEvent.Source source) {
        //动态执行表达式
        CleanUserCache cleanUserCache = source.getMethod().getAnnotation(CleanUserCache.class);

        Map<String, Object> parmMap = new HashMap<>();
        String[] parmNames = source.getParmName();
        Object[] parmValues = source.getParm();

        for (int i = 0; i < parmNames.length; i++) {
            parmMap.put(parmNames[i], parmValues[i]);
        }

        //取出所有参数
        Object val = SpringELUtil.parseExpression(parmMap, cleanUserCache.value());
        if (val == null) {
            return;
        }

        //集合支持
        Set<String> rets = new HashSet<>();
        if (val instanceof Collection) {
            rets.addAll((Collection) val);
        } else if (val.getClass().isArray()) {
            rets.addAll(Arrays.asList((String[]) val));
        } else {
            rets.add(String.valueOf(val));
        }


        //添加到需要清空的缓存队列里
        addCleanCacheSet(rets, cleanUserCache.type());
    }

    private void addCleanCacheSet(Set<String> ret, CleanUserCacheType cacheType) {
        if (cacheType == CleanUserCacheType.User) {
            ret.forEach((it) -> {
                this.cacheCleanHelper.addUser(it);
            });
        } else if (cacheType == CleanUserCacheType.Role) {
            ret.forEach((it) -> {
                this.cacheCleanHelper.addRole(it);
            });
        } else if (cacheType == CleanUserCacheType.Enterprise) {
            ret.forEach((it) -> {
                this.cacheCleanHelper.addEnterprise(it);
            });
        }

    }


}
