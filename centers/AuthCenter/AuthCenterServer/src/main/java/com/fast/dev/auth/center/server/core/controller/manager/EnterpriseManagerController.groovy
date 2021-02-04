package com.fast.dev.auth.center.server.core.controller.manager


import com.fast.dev.auth.client.service.EnterpriseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manager/enterprise")
class EnterpriseManagerController {

    @Autowired
    @Delegate(methodAnnotations = true, parameterAnnotations = true, includeTypes = EnterpriseService.class, interfaces = false)
    private EnterpriseService enterpriseService;


}
