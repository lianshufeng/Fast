package com.fast.dev.pay.server.core.general.service.pay.impl.weixin


import com.fast.dev.pay.client.type.PayMethod
import com.fast.dev.pay.server.core.general.service.pay.PaySupportService
import org.springframework.stereotype.Service

@Service
public class WeiXinJsApiPaySupportService extends WeiXinSuperPaySupportService implements PaySupportService {

    @Override
    public PayMethod payMethod() {
        return PayMethod.WeiXinJsApi;
    }


}
