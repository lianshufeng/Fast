package com.fast.dev.ucenter.security.stream;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.ucenter.core.model.UserMessage;
import com.fast.dev.ucenter.core.stream.UserCenterInputStream;
import com.fast.dev.ucenter.security.cache.UserTokenCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;

import javax.annotation.Resource;

/**
 * 用于接收消息
 */
@Slf4j
@EnableBinding(value = {UserCenterInputStream.class})
public class UserStream {

    @Resource
    private UserTokenCache userTokenCache;

    protected final static String LogTemplate = "[UserNotice] - %s";

    @StreamListener(value = UserCenterInputStream.name)
    public void receive(@Payload UserMessage userMessage) {
        log.debug(String.format(LogTemplate, JsonUtil.toJson(userMessage)));
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
        }

    }


    /**
     * 启用用户
     *
     * @param userMessage
     */
    private void enable(UserMessage userMessage) {

    }


    /**
     * 禁用用户,清空缓存
     *
     * @param userMessage
     */
    private void disable(UserMessage userMessage) {
        this.userTokenCache.cleanUserCache(userMessage.getUid());
    }

    /**
     * 注销
     *
     * @param userMessage
     */
    private void logout(UserMessage userMessage) {
        //清除缓存
        this.userTokenCache.remove(userMessage.getuToken());
    }

    /**
     * 登陆
     *
     * @param userMessage
     */
    private void login(UserMessage userMessage) {

    }

    /**
     * 注册
     *
     * @param userMessage
     */
    private void register(UserMessage userMessage) {

    }

}
