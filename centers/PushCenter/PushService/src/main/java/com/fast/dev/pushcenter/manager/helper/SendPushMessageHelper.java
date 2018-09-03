package com.fast.dev.pushcenter.manager.helper;

import com.fast.dev.pushcenter.manager.model.PlatformMessage;
import com.fast.dev.pushcenter.manager.model.UserMessage;
import com.fast.dev.pushcenter.manager.stream.PushCenterOuputStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

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
        PushCenterOuputStream.publish(output, message);
    }


    /**
     * 推送平台消息
     *
     * @param message
     */
    public void pushPlatformMessage(PlatformMessage message) {
        PushCenterOuputStream.publish(output, message);
    }


}
