package com.fast.dev.auth.center.server.core.controller.manager


import com.fast.dev.auth.client.service.FamilyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manager/family")
class FamilyManagerController {

    @Autowired
    @Delegate(methodAnnotations = true, parameterAnnotations = true, includeTypes = FamilyService.class, interfaces = false)
    private FamilyService familyService;

}
