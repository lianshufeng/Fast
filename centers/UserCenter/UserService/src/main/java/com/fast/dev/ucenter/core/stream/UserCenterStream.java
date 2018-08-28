package com.fast.dev.ucenter.core.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 作者：练书锋
 * 时间：2018/8/28
 * 用户信息流
 */
public interface UserCenterStream {

    /**
     * 用户令牌注销
     */
    public static final String TokenLogout = "UserCenterStream";

    /**
     * 读取消息
     *
     * @return
     */
    @Input(TokenLogout)
    SubscribableChannel input();


    /**
     * 给订阅的管道发送消息
     *
     * @return
     */
    @Output(TokenLogout)
    MessageChannel output();

}
