package com.fast.dev.ucenter.core.helper;

import com.fast.dev.ucenter.core.domain.UserToken;
import com.fast.dev.ucenter.core.model.UserMessage;
import com.fast.dev.ucenter.core.type.UserMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户状态变更推送
 */
@Component
public class UserPushMessageHelper {


    @Autowired
    private UserCenterOuputStreamHelper userCenterOuputStreamHelper;

    /**
     * 推送消息
     *
     * @return
     */
    public boolean push(UserMessage userMessage) {
        return userCenterOuputStreamHelper.publish(userMessage);
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


    /**
     * 推送用户被允许的通知
     *
     * @param uid
     * @return
     */
    public boolean pushUserEnable(String uid) {
        return push(new UserMessage(uid, null, UserMessageType.Enable));
    }


    /**
     * 用户被禁止
     *
     * @param uid
     * @return
     */
    public boolean pushUserDisable(String uid) {
        return push(new UserMessage(uid, null, UserMessageType.Disable));
    }


}
