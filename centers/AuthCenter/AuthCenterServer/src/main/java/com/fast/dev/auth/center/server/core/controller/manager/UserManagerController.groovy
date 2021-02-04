package com.fast.dev.auth.center.server.core.controller.manager


import com.fast.dev.auth.client.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manager/user")
class UserManagerController {

    @Autowired
    @Delegate(methodAnnotations = true, parameterAnnotations = true, includeTypes = UserService.class, interfaces = false)
    private UserService userService;


}
