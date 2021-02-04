package com.fast.dev.auth.security;

import com.fast.dev.auth.security.model.UserParmModel;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log
@Component
public class AuthClientUserHelper {


    /**
     * 获取当前的登录用户信息
     *
     * @return
     */
    public Optional<UserParmModel> getUserOptional() {
        return Optional.ofNullable(this.getUser());
    }


    /**
     * 获取当前用户的参数
     *
     * @return
     */
    public UserParmModel getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        //转换为User对象
        Object details = authentication.getDetails();
        if (details != null && details instanceof UserParmModel) {
            return (UserParmModel) details;
        }
        return null;
    }


}
