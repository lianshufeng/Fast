package com.fast.dev.pushcenter.manager.helper;

import com.fast.dev.pushcenter.manager.model.BaseMessage;
import com.fast.dev.pushcenter.manager.model.PlatformMessage;
import com.fast.dev.pushcenter.manager.model.UserMessage;
import com.fast.dev.pushcenter.manager.stream.PushCenterOuputStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;

/**
 * 作者：练书锋
 * 时间：2018/9/3
 * 推送平台的发送
 */
@EnableBinding(value = {PushCenterOuputStream.class})
public class SendPushMessageHelper {

    @Resource
    @Output(PushCenterOuputStream.name)
    MessageChannel output;


    /**
     * 推送消息
     *
     * @return
     */
    public void pushUserMessage(UserMessage message) {
        publish(output, message);
    }


    /**
     * 推送平台消息
     *
     * @param message
     */
    public void pushPlatformMessage(PlatformMessage message) {
        publish(output, message);
    }



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
