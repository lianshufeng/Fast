package com.fast.dev.auth.center.server.core.listen.stream;

import com.fast.dev.auth.center.server.core.service.impl.UserServiceImpl;
import com.fast.dev.ucenter.core.model.UserMessage;
import com.fast.dev.ucenter.core.stream.UserCenterInputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Optional;

/**
 * 用于接收消息
 */
@Slf4j
@EnableBinding(value = {UserCenterInputStream.class})
public class UserCenterStream {

    @Autowired
    private UserServiceImpl userService;


    @StreamListener(value = UserCenterInputStream.name)
    public void receive(@Payload UserMessage userMessage) {
        //类型为注销
        switch (userMessage.getType()) {
            case Register:
                break;
            case Login:
                break;
            case Logout:
                break;
            case Enable:
                break;
            case Disable:
                break;
            case UpdateLoginName:
                this.UpdateLoginName(userMessage);
                break;
        }
    }


    /**
     * 更新登录名
     *
     * @param userMessage
     */
    private void UpdateLoginName(UserMessage userMessage) {
        Optional.ofNullable(userMessage.getUid()).ifPresent((uid) -> {
            this.userService.updateLoginName(uid);
        });
    }


}
