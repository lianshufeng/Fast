package com.fast.dev.mq.mqserver.core.controller;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.mq.mqserver.core.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RequestMapping("token")
//@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    /**
     * token是否存在
     *
     * @param token
     * @return
     */
    @RequestMapping(value = {"refresh", "exist"})
    public InvokerResult<Boolean> exist(String token) {
        Assert.hasText(token, "token不能为空");
        return InvokerResult.notNull(this.tokenService.update(token));
    }


}
