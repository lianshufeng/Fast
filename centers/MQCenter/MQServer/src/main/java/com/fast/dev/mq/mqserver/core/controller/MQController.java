package com.fast.dev.mq.mqserver.core.controller;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.mq.mqserver.core.conf.MQConf;
import com.fast.dev.mq.mqserver.core.model.WelcomeModel;
import com.fast.dev.mq.mqserver.core.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class MQController {

    @Autowired
    private MQConf mqConfig;


    @RequestMapping(value = {"/", ""})
    public InvokerResult<WelcomeModel> list() {
        String token = TokenUtil.create();
        WelcomeModel welcomeModel = new WelcomeModel();
        welcomeModel.setToken(token);
        welcomeModel.setHosts(this.mqConfig.getOuterHosts());
        welcomeModel.setUserName(mqConfig.getOuterUserName());
        welcomeModel.setPassWord(mqConfig.getOuterPassWord());
        return InvokerResult.success(welcomeModel);
    }


}
