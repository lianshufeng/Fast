package com.fast.dev.ucenter.core.service.impl;

import com.fast.dev.ucenter.core.batch.BatchUserService;
import com.fast.dev.ucenter.core.dao.mongo.BaseUserDao;
import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.model.BaseUserModel;
import com.fast.dev.ucenter.core.model.batch.BatchQueryLoginNameModel;
import com.fast.dev.ucenter.core.model.batch.BatchQueryValue;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BatchUserServiceImpl implements BatchUserService {


    @Autowired
    private BaseUserDao baseUserDao;


    @Override
    public Map<String, BaseUserModel> queryUserId(BatchQueryValue value) {
        List<BaseUser> items = baseUserDao.batchQuery(value.getValues().stream().map((id) -> {
            return new HashMap<String, Object>() {{
                put("_id", id);
            }};
        }).collect(Collectors.toList()));
        Map<String, BaseUserModel> ret = new HashMap<>();
        items.stream().forEach(((it) -> {
            ret.put(it.getId(), UserManagerImpl.copyToBaseUserModel(it));
        }));
        return ret;
    }

    @Override
    public List<BaseUserModel> queryByLoginName(BatchQueryLoginNameModel queryLoginNameModel) {
        return baseUserDao.batchQuery(Arrays.stream(queryLoginNameModel.getItems()).map((it) -> {
            return new HashMap<String, Object>() {{
                put(it.getLoginType().getUserLoginTypeName(), it.getLoginName());
            }};
        }).collect(Collectors.toList())).stream().map((it) -> {
            return UserManagerImpl.copyToBaseUserModel(it);
        }).collect(Collectors.toList());

    }
}
