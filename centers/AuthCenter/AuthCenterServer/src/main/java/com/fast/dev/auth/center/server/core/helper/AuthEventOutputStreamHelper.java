package com.fast.dev.auth.center.server.core.helper;

import com.fast.dev.auth.client.model.AuthEventModel;
import com.fast.dev.auth.client.stream.AuthEventOutputStream;
import com.fast.dev.auth.client.type.AuthEventAction;
import com.fast.dev.auth.client.type.AuthEventType;
import lombok.extern.java.Log;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@Log
@EnableBinding(value = {AuthEventOutputStream.class})
public class AuthEventOutputStreamHelper {


    @Resource
    @Output(AuthEventOutputStream.name)
    private MessageChannel output;


    /**
     * 发布事件
     *
     * @param authEvent
     */
    public void publish(AuthEventModel authEvent) {
        output.send(MessageBuilder.withPayload(authEvent).build());
    }


    /**
     * 发布事件
     *
     * @param type
     * @param action
     * @param data
     */
    public void publish(AuthEventType type, AuthEventAction action, Map<String, Object> data) {
        AuthEventModel authEventModel = new AuthEventModel();
        authEventModel.setType(type);
        authEventModel.setAction(action);
        authEventModel.setData(data);
        this.publish(authEventModel);
    }


}
