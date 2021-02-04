package com.fast.dev.auth.center.server.core.controller.manager

import com.fast.dev.auth.client.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manager/user/auth")
class UserAuthManagerController {

    @Autowired
    @Delegate(methodAnnotations = true, parameterAnnotations = true, includeTypes = AuthService.class, interfaces = false)
    private AuthService authService;


}
