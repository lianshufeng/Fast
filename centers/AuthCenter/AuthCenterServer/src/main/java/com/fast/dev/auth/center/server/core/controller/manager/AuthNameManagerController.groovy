package com.fast.dev.auth.center.server.core.controller.manager

import com.fast.dev.auth.client.service.AuthNameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manager/auth/name")
class AuthNameManagerController {

    @Autowired
    @Delegate(methodAnnotations = true, parameterAnnotations = true, includeTypes = AuthNameService.class, interfaces = false)
    private AuthNameService authNameService;


}
