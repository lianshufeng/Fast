package com.fast.dev.pushcenter.manager.helper;

import com.fast.dev.pushcenter.manager.model.PlatformMessage;
import com.fast.dev.pushcenter.manager.stream.PushCenterInputStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * 用于接收消息
 */
@EnableBinding(value = {PushCenterInputStream.class})
public abstract class ReceivePushMessageHelper {


    /**
     * 接收到平台推送
     *
     * @param message
     */
    @StreamListener(value = PushCenterInputStream.name, condition = "new java.lang.String(headers['PushMessageType']) =='PlatformMessage'")
    public abstract void receivePlatformMessage(@Payload PlatformMessage message);


}
