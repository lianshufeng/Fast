package com.fast.dev.ucenter.core.stream;

import com.fast.dev.ucenter.core.model.UserMessage;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

/**
 * 消息模版
 */
public interface UserCenterOuputStream {


    public static String name = "UserCenterOuputStream";

    @Output(name)
    MessageChannel messageChannel();


    /**
     * 发布
     *
     * @param
     */
    public static boolean publish(final MessageChannel messageChannel, UserMessage body) {
        Message message = MessageBuilder.withPayload(body).build();
        return messageChannel.send(message);
    }


}
