package com.fast.dev.ucenter.core.controller.manager

import com.fast.dev.ucenter.core.batch.BatchUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/manager/batch")
class BatchUserManagerController {

    @Autowired
    @Delegate(methodAnnotations = true, parameterAnnotations = true, includeTypes = BatchUserService.class, interfaces = false)
    private BatchUserService batchUserService;

}
