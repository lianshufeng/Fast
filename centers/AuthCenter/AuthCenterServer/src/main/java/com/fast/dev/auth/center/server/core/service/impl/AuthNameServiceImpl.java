package com.fast.dev.auth.center.server.core.service.impl;

import com.fast.dev.auth.center.server.core.conf.DefaultRoleConf;
import com.fast.dev.auth.center.server.core.dao.AuthNameDao;
import com.fast.dev.auth.center.server.core.domain.AuthName;
import com.fast.dev.auth.client.model.AuthNameModel;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.client.service.AuthNameService;
import com.fast.dev.data.base.util.PageEntityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class AuthNameServiceImpl implements AuthNameService {

    @Autowired
    private DefaultRoleConf defaultRoleConf;

    @Autowired
    private AuthNameDao authNameDao;

    @Override
    public ResultContent add(AuthNameModel[] authName) {
        return ResultContent.build(authNameDao.put(Arrays.stream(authName).collect(Collectors.toMap(AuthNameModel::getName, AuthNameModel::getRemark))) > 0);
    }

    @Override
    public ResultContent remove(String[] authName) {
        return ResultContent.build(authNameDao.remove(authName) > 0);
    }

    @Override
    public Page<AuthNameModel> list(String name, String remark, Pageable pageable) {
        AuthNameModel authNameModel = new AuthNameModel();
        authNameModel.setName(name);
        authNameModel.setRemark(remark);
        return PageEntityUtil.concurrent2PageModel(authNameDao.list(authNameModel, this.defaultRoleConf.getAuthNameBlacklist(), pageable), (it) -> {
            return toModel(it);
        });
    }


    private AuthNameModel toModel(AuthName authName) {
        AuthNameModel authNameModel = new AuthNameModel();
        BeanUtils.copyProperties(authName, authNameModel);
        return authNameModel;
    }


}
