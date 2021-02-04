package com.fast.dev.auth.client.helper;

import com.fast.dev.auth.client.log.model.UserLogModel;
import com.fast.dev.auth.client.stream.UserLogOutputStream;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;

@Log
@EnableBinding(value = {UserLogOutputStream.class})
public class UserLogOutputStreamHelper {

    //懒发布时间
    private static long lazyTime = 3000;

    @Resource
    @Output(UserLogOutputStream.name)
    MessageChannel output;

    //定时器
    private Timer timer = new Timer();

    //保存用户日志
    private Vector<UserLogModel> vectors = new Vector<>();


    @PreDestroy
    private void shutdown() {
        sendUserLog();
    }


    @Autowired
    private void init() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendUserLog();
            }
        }, lazyTime, lazyTime);

    }


    /**
     * 发布
     *
     * @param
     */
    public boolean publish(UserLogModel... body) {
        return vectors.addAll(Arrays.asList(body));
    }


    /**
     * 发送到kafka里
     */
    private void sendUserLog() {
        if (vectors.size() == 0) {
            return;
        }

        Set<UserLogModel> sets = new HashSet<>();
        while (vectors.size() > 0) {
            sets.add(vectors.remove(0));
        }
        log.info("send user log : " + sets.size());
        output.send(MessageBuilder.withPayload(sets.toArray(new UserLogModel[0])).build());
    }


}
