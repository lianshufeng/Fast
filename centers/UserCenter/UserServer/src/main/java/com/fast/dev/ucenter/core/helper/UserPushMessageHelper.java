package com.fast.dev.ucenter.core.helper;

import com.fast.dev.ucenter.core.domain.UserToken;
import com.fast.dev.ucenter.core.model.UserMessage;
import com.fast.dev.ucenter.core.stream.UserCenterOuputStream;
import com.fast.dev.ucenter.core.type.UserMessageType;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户状态变更推送
 */
@Component
@EnableBinding(value = {UserCenterOuputStream.class})
public class UserPushMessageHelper {


    @Resource
    @Output(UserCenterOuputStream.name)
    MessageChannel output;

    /**
     * 推送消息
     *
     * @return
     */
    private boolean push(UserMessage userMessage) {
        return UserCenterOuputStream.publish(output, userMessage);
    }


    /**
     * 注销
     *
     * @param userToken
     * @return
     */
    public boolean pushLogoutMsg(UserToken userToken) {
        return push(new UserMessage(userToken.getUid(), userToken.getToken(), UserMessageType.Logout));
    }


    /**
     * 登陆
     *
     * @param userToken
     * @return
     */
    public boolean pushLoginMsg(UserToken userToken) {
        return push(new UserMessage(userToken.getUid(), userToken.getToken(), UserMessageType.Login));
    }

    /**
     * 注册
     *
     * @return
     */
    public boolean pushRegisterMsg(String uid) {
        return push(new UserMessage(uid, null, UserMessageType.Register));
    }


}
