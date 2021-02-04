package com.fast.dev.mq.mqserver.core.rev;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.mq.mqserver.core.conf.MQConf;
import com.fast.dev.mq.mqserver.core.constant.MQConstant;
import com.fast.dev.mq.mqserver.core.helper.MessageLockHelper;
import com.fast.dev.mq.mqserver.core.model.RequestModel;
import com.fast.dev.mq.mqserver.core.service.RemoteCallService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.jms.BytesMessage;
import javax.jms.Message;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class RequestListenner {

    @Autowired
    private MQConf mqConf;


    @Autowired
    private MessageLockHelper messageLockHelper;

    //线程池
    ExecutorService threadPool;

    @Autowired
    private RemoteCallService remoteCallService;

    @Autowired
    private void init() {
        this.threadPool = Executors.newFixedThreadPool(mqConf.getMessageThreadPoolCount());
    }

    @PreDestroy
    private void shutdown() {
        this.threadPool.shutdownNow();
    }

//    @Autowired
//    private TokenService tokenService;


    //接收topic消息
    @SneakyThrows
    @JmsListener(destination = MQConstant.RequetTopic, containerFactory = MQConstant.TopicListenerContainerFactory)
    public void handlerTopic(BytesMessage message) {

        //取出消息id
        String msgId = message.getJMSMessageID().replaceAll(":", "").replaceAll("-", "");

        //抢占消息
        if (this.messageLockHelper.lock(msgId) == false) {
            log.debug("抢占消息处理失败 : " + msgId);
            return;
        }


        //使用线程池
        threadPool.execute(() -> {
            RequestModel requestModel = toRequestModel(message);
            if (messageTimeOut(message, requestModel.getTimeOut())) {
                log.info("message timeOut: " + requestModel);
                return;
            }

            //转发请求
            remoteCallService.call(requestModel);

        });

    }


    @SneakyThrows
    private RequestModel toRequestModel(BytesMessage message) {
        //转换为消息对象
        byte[] buffer = new byte[Integer.parseInt(String.valueOf(message.getBodyLength()))];
        message.readBytes(buffer);
        String text = new String(buffer, MQConstant.CharSet);
        return JsonUtil.toObject(text, RequestModel.class);
    }


    @SneakyThrows
    private boolean messageTimeOut(Message message, long timeOut) {
        return (timeOut + message.getJMSTimestamp()) < System.currentTimeMillis();
    }


}
