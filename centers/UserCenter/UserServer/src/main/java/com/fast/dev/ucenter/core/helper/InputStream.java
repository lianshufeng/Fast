package com.fast.dev.ucenter.core.helper;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.ucenter.core.model.UserMessage;
import com.fast.dev.ucenter.core.stream.UserCenterInputStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

@EnableBinding(UserCenterInputStream.class)
public class InputStream {


    @StreamListener(UserCenterInputStream.name)
    public void input(Message<UserMessage> message) throws Exception {
        System.out.println(Thread.currentThread() + " input : " + JsonUtil.toJson(message.getPayload()));
    }


}
