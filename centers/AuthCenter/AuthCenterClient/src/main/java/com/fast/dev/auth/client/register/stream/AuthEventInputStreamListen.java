package com.fast.dev.auth.client.register.stream;

import com.fast.dev.auth.client.model.AuthEventModel;
import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.auth.client.register.helper.AuthRegisterHelper;
import com.fast.dev.auth.client.stream.AuthEventInputStream;
import com.fast.dev.auth.client.type.AuthEventAction;
import com.fast.dev.auth.client.type.AuthEventType;
import com.fast.dev.core.util.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Map;

@Slf4j
@EnableBinding(value = {AuthEventInputStream.class})
public class AuthEventInputStreamListen {


    @Autowired
    private AuthRegisterHelper authRegisterHelper;

    @SneakyThrows
    @StreamListener(value = AuthEventInputStream.name)
    public void receive(@Payload AuthEventModel authEventModel) {
        try {
            execute(authEventModel);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("exception : {} ", e.getMessage());
        }
    }

    @SneakyThrows
    private void execute(AuthEventModel authEventModel) {
        if (authEventModel.getType() == AuthEventType.Enterprise && authEventModel.getAction() == AuthEventAction.Create) {
            Map<String, Object> data = authEventModel.getData();

            //企业id
            String epId = String.valueOf(data.get("ret.content"));

            //企业的拥有者
            String ownerUid = String.valueOf(data.get("ownerUid"));

            //企业模型
            EnterpriseModel enterpriseModel = JsonUtil.toObject(JsonUtil.toJson(data.get("model")), EnterpriseModel.class);
            enterpriseModel.setId(epId);

            //企业初始化
            this.authRegisterHelper.enterpriseInit(enterpriseModel, ownerUid);
        }

    }


}
