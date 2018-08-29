package com.fast.dev.ucenter.security.stream;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.ucenter.core.model.UserMessage;
import com.fast.dev.ucenter.core.stream.UserCenterInputStream;
import com.fast.dev.ucenter.security.cache.UserTokenCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;

import javax.annotation.Resource;

/**
 * 用于接收消息
 */
@EnableBinding(value = {UserCenterInputStream.class})
public class UserStream {

    private static final Logger logger = LoggerFactory.getLogger(UserStream.class);

    @Resource
    private UserTokenCache userTokenCache;

    private final static String LogTemplate = "[UserNotice] - %s";

    @StreamListener(value = UserCenterInputStream.name)
    public void receive(@Payload UserMessage userMessage) {
        logger.info(String.format(LogTemplate, JsonUtil.toJson(userMessage)));
        //类型为注销
        switch (userMessage.getType()) {
            case Register:
                register(userMessage);
                break;
            case Login:
                login(userMessage);
                break;
            case Logout:
                logout(userMessage);
                break;
        }

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
