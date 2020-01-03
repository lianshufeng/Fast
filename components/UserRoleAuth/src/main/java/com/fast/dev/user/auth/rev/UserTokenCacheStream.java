package com.fast.dev.user.auth.rev;

import com.fast.dev.ucenter.security.cache.UserTokenCache;
import com.fast.dev.user.auth.stream.UserTokenCacheInputStream;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.List;
import java.util.Map;

/**
 * 用于接收消息
 */
@Log
@EnableBinding(value = {UserTokenCacheInputStream.class})
public class UserTokenCacheStream {


    @Autowired
    private UserTokenCache userTokenCache;


    //接收到消息，清空缓存
    @StreamListener(value = UserTokenCacheInputStream.name)
    public void receive(@Payload Map<String, Object> o) {
        String action = String.valueOf(o.get("action"));
        if (action.equals("cleanUser")) {
            this.userTokenCache.cleanUserCache(((List<String>) o.get("uid")).toArray(new String[0]));
        } else if (action.equals("cleanAll")) {
            this.userTokenCache.cleanAllCache();
        }
    }


}
