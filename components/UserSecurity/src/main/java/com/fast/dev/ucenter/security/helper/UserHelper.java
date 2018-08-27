package com.fast.dev.ucenter.security.helper;

import com.fast.dev.ucenter.security.model.UserAuth;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserHelper {


    /**
     * 获取当前用户
     *
     * @return
     */
    public UserAuth getUser() {
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            return (UserAuth) authentication.getDetails();
        }
        return null;
    }

    /**
     * 获取当前用户id
     *
     * @return
     */
    public String getUserId() {
        UserAuth userAuth = getUser();
        if (userAuth != null) {
            return userAuth.getUid();
        }
        return null;
    }


    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


}
