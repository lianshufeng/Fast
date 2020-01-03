package com.fast.dev.user.auth.helper;

import com.fast.dev.user.auth.stream.UserTokenCacheOutputStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户中心发送消息
 */

@EnableBinding(value = {UserTokenCacheOutputStream.class})
public class UserTokenCacheStreamHelper {


    @Resource
    @Output(UserTokenCacheOutputStream.name)
    MessageChannel output;



    /**
     * 清除用户
     *
     * @param uid
     * @return
     */
    public boolean cleanUser(String... uid) {
        Map<String, Object> o = new HashMap<>();
        o.put("action", "cleanUser");
        o.put("uid", uid);
        Message message = MessageBuilder.withPayload(o).build();
        return output.send(message);
    }


    /**
     * 清除所有的
     *
     * @return
     */
    public boolean cleanAll() {
        Map<String, Object> o = new HashMap<>();
        o.put("action", "cleanAll");
        Message message = MessageBuilder.withPayload(o).build();
        return output.send(message);
    }


}
