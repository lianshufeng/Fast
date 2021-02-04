package com.fast.dev.timercenter.server.core.controller.api

import com.fast.dev.timercenter.service.service.RemoteManagerTimerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/manager")
class TimerCenterManagerController {

    @Autowired
    @Delegate(methodAnnotations = true, parameterAnnotations = true, includeTypes = RemoteManagerTimerService.class, interfaces = false)
    public RemoteManagerTimerService remoteUserTimerService;


}
