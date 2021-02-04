package com.fast.dev.mq.mqserver.core.controller.manager;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.mq.mqserver.core.client.ActiveMQClient;
import com.fast.dev.mq.mqserver.core.model.SendModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("manager")
public class MQManagerController {

    @Autowired
    private ActiveMQClient activeMQClient;


    @RequestMapping("send")
    public InvokerResult<Object> send(@RequestBody SendModel sendModel) {
        this.activeMQClient.sendData(sendModel.getTopic(), sendModel.getParm());
        return InvokerResult.success(System.currentTimeMillis());
    }


}
