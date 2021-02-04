package com.fast.dev.auth.center.server.core.dao.impl;

import com.fast.dev.auth.center.server.core.dao.extend.IdentityNameDaoExtend;
import com.fast.dev.auth.center.server.core.domain.IdentityName;
import com.fast.dev.data.mongo.domain.SuperEntity;

public class IdentityNameDaoImpl extends InternalResourceDaoImpl<IdentityName> implements IdentityNameDaoExtend {
    @Override
    public Class<? extends SuperEntity> entityClass() {
        return IdentityName.class;
    }
}
