package com.fast.dev.pushcenter.manager.stream;

import com.fast.dev.pushcenter.manager.model.BaseMessage;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

/**
 * 消息模版
 */
public interface PushCenterOuputStream {


    public static String name = "PushCenterOuputStream";

    @Output(name)
    MessageChannel messageChannel();


    /**
     * 发布推送的消息
     *
     * @param
     */
    public static boolean publish(final MessageChannel messageChannel, BaseMessage baseMessage) {
        Message message = MessageBuilder.withPayload(baseMessage)
                .setHeader("PushMessageType", baseMessage.getClass().getSimpleName())
                .build();
        return messageChannel.send(message);
    }


}
