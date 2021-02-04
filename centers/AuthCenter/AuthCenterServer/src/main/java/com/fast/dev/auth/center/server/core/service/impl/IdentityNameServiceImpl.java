package com.fast.dev.auth.center.server.core.service.impl;

import com.fast.dev.auth.center.server.core.dao.IdentityNameDao;
import com.fast.dev.auth.center.server.core.domain.IdentityName;
import com.fast.dev.auth.client.model.AuthNameModel;
import com.fast.dev.auth.client.model.IdentityNameModel;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.client.service.IdentityNameService;
import com.fast.dev.data.base.util.PageEntityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class IdentityNameServiceImpl implements IdentityNameService {


    @Autowired
    private IdentityNameDao identityNameDao;

    @Override
    public ResultContent add(IdentityNameModel[] identityName) {
        return ResultContent.build(identityNameDao.put(Arrays.stream(identityName).collect(Collectors.toMap(IdentityNameModel::getName, IdentityNameModel::getRemark))) > 0);
    }

    @Override
    public ResultContent remove(String[] identityName) {
        return ResultContent.build(identityNameDao.remove(identityName) > 0);
    }

    @Override
    public Page<IdentityNameModel> list(String name, String remark, Pageable pageable) {
        IdentityNameModel identityNameModel = new IdentityNameModel();
        identityNameModel.setName(name);
        identityNameModel.setRemark(remark);
        return PageEntityUtil.concurrent2PageModel(identityNameDao.list(identityNameModel,new HashSet<String>(), pageable), (it) -> {
            return toModel(it);
        });
    }


    private IdentityNameModel toModel(IdentityName identityName) {
        IdentityNameModel identityNameModel = new IdentityNameModel();
        BeanUtils.copyProperties(identityName, identityNameModel);
        return identityNameModel;
    }
}
