package com.fast.dev.auth.center.server.core.stream;

import com.fast.dev.auth.center.server.core.service.impl.UserLogServiceImpl;
import com.fast.dev.auth.client.log.model.UserLogModel;
import com.fast.dev.auth.client.stream.UserLogInputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;

@Slf4j
@EnableBinding(value = {UserLogInputStream.class})
public class UserLogStreamListener {

    @Autowired
    private UserLogServiceImpl userLogService;

    @StreamListener(value = UserLogInputStream.name)
    public void receive(@Payload UserLogModel... userLogModel) {
        if (userLogModel == null || userLogModel.length == 0) {
            return;
        }
        log.info("rev uselog : " + userLogModel.length);
        this.userLogService.add(userLogModel);
    }


}
