package com.fast.dev.pay.server.core.general.controller.manager


import com.fast.dev.pay.client.service.UserBankCardService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("manager/user/bankcard")
class UserBankCardController {

    @Autowired
    @Delegate(methodAnnotations = true, parameterAnnotations = true, includeTypes = UserBankCardService.class, interfaces = false)
    private UserBankCardService userBankCardService;
}
