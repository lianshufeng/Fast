package com.fast.dev.auth.security.stream;

import com.fast.dev.auth.security.cache.AuthClientUserTokenCache;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.ucenter.core.model.UserMessage;
import com.fast.dev.ucenter.security.stream.UserStream;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log
@Component
public class AuthClientUserStream extends UserStream {


    @Autowired
    private AuthClientUserTokenCache authClientUserTokenCache;


    @Override
    public void receive(UserMessage userMessage) {
        log.info(String.format(UserStream.LogTemplate, JsonUtil.toJson(userMessage)));
        //类型为注销
        switch (userMessage.getType()) {
            case Register:
                this.register(userMessage);
                break;
            case Login:
                this.login(userMessage);
                break;
            case Logout:
                this.logout(userMessage);
                break;
            case Enable:
                this.enable(userMessage);
                break;
            case Disable:
                this.disable(userMessage);
                break;
            case CleanCache:
                this.cleanCache(userMessage);
                break;
        }
    }


    /**
     * 清除缓存
     *
     * @param userMessage
     */
    @SneakyThrows
    private void cleanCache(UserMessage userMessage) {
        this.authClientUserTokenCache.cleanUserCache(userMessage.getUid());
    }

    /**
     * 启用用户
     *
     * @param userMessage
     */
    @SneakyThrows
    private void enable(UserMessage userMessage) {

    }


    /**
     * 禁用用户,清空缓存
     *
     * @param userMessage
     */
    @SneakyThrows
    private void disable(UserMessage userMessage) {
        this.authClientUserTokenCache.cleanUserCache(userMessage.getUid());
    }

    /**
     * 注销
     *
     * @param userMessage
     */
    @SneakyThrows
    private void logout(UserMessage userMessage) {
        //清除缓存
        this.authClientUserTokenCache.remove(userMessage.getuToken());
    }

    /**
     * 登陆
     *
     * @param userMessage
     */
    @SneakyThrows
    private void login(UserMessage userMessage) {

    }

    /**
     * 注册
     *
     * @param userMessage
     */
    @SneakyThrows
    private void register(UserMessage userMessage) {

    }

}
