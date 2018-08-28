package com.fast.dev.ucenter.core.service;

import com.fast.dev.ucenter.core.dao.UserTokenDao;
import com.fast.dev.ucenter.core.domain.UserToken;
import com.fast.dev.ucenter.core.helper.UserPushMessageHelper;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.core.util.BaseTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserManagerImpl extends BaseUserService implements UserManagerService {

    @Autowired
    private UserTokenDao userTokenDao;


    @Autowired
    private UserPushMessageHelper userPushMessageHelper;


    @Override
    public UserTokenModel queryByUserToken(String token) {
        UserToken userToken = this.userTokenDao.queryOnly(token);
        return BaseTokenUtil.toUserTokenModel(userToken);
    }

}
