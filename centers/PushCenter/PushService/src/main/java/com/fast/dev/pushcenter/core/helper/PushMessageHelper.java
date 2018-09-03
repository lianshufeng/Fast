package com.fast.dev.pushcenter.core.helper;

import com.fast.dev.pushcenter.core.model.BaseMessage;
import com.fast.dev.pushcenter.core.model.PlatformMessage;
import com.fast.dev.pushcenter.core.model.UserMessage;
import com.fast.dev.pushcenter.core.stream.PushCenterOuputStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 作者：练书锋
 * 时间：2018/9/3
 * 推送平台的发送
 */
@Component
@EnableBinding(value = {PushCenterOuputStream.class})
public class PushMessageHelper {

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
