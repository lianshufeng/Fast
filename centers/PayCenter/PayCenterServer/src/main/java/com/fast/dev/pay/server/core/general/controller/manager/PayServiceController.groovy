package com.fast.dev.pay.server.core.general.controller.manager


import com.fast.dev.pay.client.service.PayService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("manager/pay")
class PayServiceController {

    @Autowired
    @Delegate(methodAnnotations = true, parameterAnnotations = true, includeTypes = PayService.class, interfaces = false)
    private PayService payService;

}
