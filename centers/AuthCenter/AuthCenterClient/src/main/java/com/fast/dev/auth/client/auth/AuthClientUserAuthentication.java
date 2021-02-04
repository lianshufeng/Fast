package com.fast.dev.auth.client.auth;

import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.security.model.UserIdentity;
import com.fast.dev.ucenter.security.service.UserAuthentication;
import org.springframework.stereotype.Component;

/**
 * 无需实现
 */
@Component
public class AuthClientUserAuthentication implements UserAuthentication {
    @Override
    public UserIdentity authentication(UserTokenModel userTokenModel) {
        return null;
    }
}
