package com.fast.dev.ucenter.security.stream;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.ucenter.core.model.UserMessage;
import com.fast.dev.ucenter.core.stream.UserCenterInputStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

/**
 * 用于接收消息
 */
@EnableBinding(value = {UserCenterInputStream.class})
public class UserStream {

    @StreamListener(UserCenterInputStream.name)
    public void receive(Message<UserMessage> msg) {
        System.out.println("Recevied:" + JsonUtil.toJson(msg.getPayload()));
    }


}
