package com.fast.dev.ucenter.core.util;

import com.fast.dev.ucenter.core.domain.UserToken;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.core.type.TokenState;

/**
 * 令牌转换工具
 */
public class BaseTokenUtil {

    /**
     * 用户令牌domain转model
     *
     * @param userToken
     * @return
     */
    public static UserTokenModel toUserTokenModel(UserToken userToken) {
        if (userToken == null) {
            return null;
        }
        UserTokenModel userTokenModel = new UserTokenModel(TokenState.Success);
        userTokenModel.setuToken(userToken.getToken());
        userTokenModel.setsToken(userToken.getSecret());
        userTokenModel.setUid(userToken.getUid());
        userTokenModel.setExpireTime(userToken.getExpireTime());
        return userTokenModel;
    }
}
