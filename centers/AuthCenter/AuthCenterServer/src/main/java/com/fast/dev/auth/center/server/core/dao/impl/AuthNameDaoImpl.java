package com.fast.dev.auth.center.server.core.dao.impl;

import com.fast.dev.auth.center.server.core.dao.extend.AuthNameDaoExtend;
import com.fast.dev.auth.center.server.core.domain.AuthName;
import com.fast.dev.data.mongo.domain.SuperEntity;

public class AuthNameDaoImpl extends InternalResourceDaoImpl<AuthName> implements AuthNameDaoExtend {


    @Override
    public Class<? extends SuperEntity> entityClass() {
        return AuthName.class;
    }


}
