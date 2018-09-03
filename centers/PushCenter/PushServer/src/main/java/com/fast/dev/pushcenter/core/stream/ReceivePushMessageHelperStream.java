package com.fast.dev.pushcenter.core.stream;

import com.fast.dev.pushcenter.core.service.remote.RemoteUserCenterService;
import com.fast.dev.pushcenter.manager.helper.ReceivePushMessageHelper;
import com.fast.dev.pushcenter.manager.helper.SendPushMessageHelper;
import com.fast.dev.pushcenter.manager.model.PlatformMessage;
import com.fast.dev.pushcenter.manager.model.UserMessage;
import com.fast.dev.pushcenter.manager.stream.PushCenterInputStream;
import com.fast.dev.pushcenter.manager.type.MessageType;
import com.fast.dev.ucenter.core.model.BaseUserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * 收到消息
 */
@Component
public class ReceivePushMessageHelperStream extends ReceivePushMessageHelper {

    private static final Logger logger = LoggerFactory.getLogger(ReceivePushMessageHelperStream.class);

    private final static String LogTemplate = "[PushMessageReceive] - %s";

    @Resource
    private SendPushMessageHelper sendPushMessageHelper;

    @Resource
    private RemoteUserCenterService userManagerService;


    @Override
    public void receivePlatformMessage(PlatformMessage message) {
        logger.info(String.format(LogTemplate, message));
    }

    /**
     * 接收到用户查询用户手机号或邮箱并转发
     *
     * @param userMessage
     */
    @StreamListener(value = PushCenterInputStream.name, condition = "headers['PushMessageType']=='UserMessage'")
    public void receiveUserMessage(@Payload UserMessage userMessage) {
        if (userMessage.getUid() == null) {
            return;
        }
        Set<String> numbers = new HashSet<>();
        for (String uid : userMessage.getUid()) {
            BaseUserModel baseUserModel = this.userManagerService.queryUserId(uid);
            addToNumbers(baseUserModel, userMessage.getMessageType(), numbers);
        }
        //成员数量
        if (numbers.size() > 0) {
            PlatformMessage platformMessage = new PlatformMessage();
            BeanUtils.copyProperties(userMessage, platformMessage);
            platformMessage.setNumber(numbers.toArray(new String[numbers.size()]));
            this.sendPushMessageHelper.pushPlatformMessage(platformMessage);
        }

    }

    /**
     * 取出号码
     *
     * @param baseUserModel
     * @param messageType
     * @return
     */
    private static void addToNumbers(BaseUserModel baseUserModel, MessageType messageType, Set<String> numbers) {
        String number = null;
        if (messageType == MessageType.Sms) {
            number = baseUserModel.getPhone();
        } else if (messageType == MessageType.Mail) {
            number = baseUserModel.getMail();
        }
        if (!StringUtils.isEmpty(number)) {
            numbers.add(number);
        }
    }

}
