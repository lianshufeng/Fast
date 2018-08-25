package com.fast.dev.ucenter.security.service.remote;

import com.fast.dev.ucenter.core.model.UserTokenModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 断路器，异常的情况处理
 */
public class RemoteUserCenterServiceError implements RemoteUserCenterService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteUserCenterService.class);

    @Override
    public UserTokenModel queryByUserToken(String token) {
        logger.error("query token : [" + token + "]  error");
        return null;
    }

    @Override
    public boolean logout(String token) {
        logger.error("logout token : [" + token + "]  error");
        return false;
    }
}
