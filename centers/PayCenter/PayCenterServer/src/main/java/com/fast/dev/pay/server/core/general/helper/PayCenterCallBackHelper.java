package com.fast.dev.pay.server.core.general.helper;

import com.fast.dev.pay.client.model.PayCallBackModel;
import com.fast.dev.pay.client.stream.PayCenterOutputStream;
import lombok.extern.java.Log;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;

/**
 * 支付中心的回调
 */
@Log
@EnableBinding(value = {PayCenterOutputStream.class})
public class PayCenterCallBackHelper {


    @Resource
    @Output(PayCenterOutputStream.name)
    MessageChannel output;


    /**
     * 发布支付回调
     *
     * @param
     */
    public boolean publish(PayCallBackModel payCallBackModel) {
        return output.send(MessageBuilder.withPayload(payCallBackModel).build());
    }


}
