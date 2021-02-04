package com.fast.dev.ucenter.core.helper;

import com.fast.dev.ucenter.core.model.UserMessage;
import com.fast.dev.ucenter.core.stream.UserCenterOuputStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;

/**
 * 用户中心发送消息
 */
@EnableBinding(value = {UserCenterOuputStream.class})
public class UserCenterOuputStreamHelper {


    @Resource
    @Output(UserCenterOuputStream.name)
    MessageChannel output;

    /**
     * 发布
     *
     * @param
     */
    public boolean publish(UserMessage body) {
        Message message = MessageBuilder.withPayload(body).build();
        return output.send(message);
    }


}
