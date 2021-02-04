package com.fast.dev.auth.center.server.core.controller.manager


import com.fast.dev.auth.client.service.IdentityNameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manager/identity/name")
class IdentityNameController {

    @Autowired
    @Delegate(methodAnnotations = true, parameterAnnotations = true, includeTypes = IdentityNameService.class, interfaces = false)
    private IdentityNameService identityNameService;

}
