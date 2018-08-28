package com.fast.dev.ucenter.security.stream;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.ucenter.core.model.UserMessage;
import com.fast.dev.ucenter.core.stream.UserCenterInputStream;
import com.fast.dev.ucenter.core.type.UserMessageType;
import com.fast.dev.ucenter.security.cache.UserTokenCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

/**
 * 用于接收消息
 */
@EnableBinding(value = {UserCenterInputStream.class})
public class UserStream {

    private static final Logger logger = LoggerFactory.getLogger(UserStream.class);

    @Autowired
    private UserTokenCache userTokenCache;

    @StreamListener(UserCenterInputStream.name)
    public void receive(Message<UserMessage> msg) {
        UserMessage userMessage = msg.getPayload();
        logger.info(JsonUtil.toJson(userMessage));
        //类型为注销
        if (userMessage.getType() == UserMessageType.Logout) {
            //清除缓存
            this.userTokenCache.remove(userMessage.getuToken());
        }
    }


}
