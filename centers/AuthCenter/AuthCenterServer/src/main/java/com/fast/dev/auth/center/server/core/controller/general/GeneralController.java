package com.fast.dev.auth.center.server.core.controller.general;

import com.fast.dev.auth.center.server.core.service.impl.AuthNameServiceImpl;
import com.fast.dev.auth.client.model.AuthNameModel;
import com.fast.dev.auth.client.model.IdentityNameModel;
import com.fast.dev.auth.client.service.IdentityNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("general")
public class GeneralController {


    @Autowired
    private AuthNameServiceImpl authNameService;

    @Autowired
    private IdentityNameService identityNameService;


    @RequestMapping("auth/list")
    public Object authList(AuthNameModel authNameModel, @PageableDefault Pageable pageable) {
        return this.authNameService.list(authNameModel.getName(), authNameModel.getRemark(), pageable);
    }

    @RequestMapping("identity/list")
    public Object identityList(IdentityNameModel identityNameModel, @PageableDefault Pageable pageable) {
        return this.identityNameService.list(identityNameModel.getName(), identityNameModel.getRemark(), pageable);
    }


}
