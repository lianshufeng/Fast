package com.fast.dev.pay.server.core.general.controller.manager

import com.fast.dev.pay.client.service.EnterprisePayAccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("manager/enterprise/pay")
class EnterpriseAccountController {

    @Autowired
    @Delegate(methodAnnotations = true, parameterAnnotations = true, includeTypes = EnterprisePayAccountService.class, interfaces = false)
    private EnterprisePayAccountService enterpriseAccountService;

}
