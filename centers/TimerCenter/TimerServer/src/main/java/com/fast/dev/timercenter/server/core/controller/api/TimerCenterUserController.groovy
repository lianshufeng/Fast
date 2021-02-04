package com.fast.dev.timercenter.server.core.controller.api


import com.fast.dev.timercenter.service.service.RemoteUserTimerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/user")
class TimerCenterUserController {

    @Autowired
    @Delegate(methodAnnotations = true, parameterAnnotations = true, includeTypes = RemoteUserTimerService.class, interfaces = false)
    public RemoteUserTimerService remoteUserTimerService;


}
