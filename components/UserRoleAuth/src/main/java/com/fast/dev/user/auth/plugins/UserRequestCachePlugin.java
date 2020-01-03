package com.fast.dev.user.auth.plugins;

import com.fast.dev.core.event.method.MethodEvent;
import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.security.helper.UserHelper;
import com.fast.dev.ucenter.security.model.UserAuth;
import com.fast.dev.user.auth.domain.User;
import com.fast.dev.user.auth.event.UserRequestEvent;
import com.fast.dev.user.auth.result.AuthCacheTokenResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UserRequestCachePlugin implements ApplicationListener<UserRequestEvent> {


    @Autowired
    private UserHelper userHelper;


    @Override
    public void onApplicationEvent(UserRequestEvent event) {


        MethodEvent.Source source = event.getSource();
        if (source.getCallType() == MethodEvent.CallType.After) {
            Object result = source.getRet();

            //结果集必须满足规则
            if (result == null || !(result instanceof InvokerResult)) {
                return;
            }

            //且当前用户已登录过
            UserAuth userAuth = this.userHelper.getUser();
            if (userAuth == null) {
                return;
            }

            //取出结果集
            Object content = ((InvokerResult) result).getContent();
            if (content instanceof AuthCacheTokenResult) {

                AuthCacheTokenResult tokenResult = (AuthCacheTokenResult) content;
                //更新权限的hash
                tokenResult.setAuthCache(String.valueOf(userAuth.getDetails().get("authHash")));

                //更新用户的缓存值，时间戳
                User u = (User) userAuth.getDetails().get("user");
                tokenResult.setUserCache(String.valueOf(u.getUpdateTime()));

            }


        }


    }
}
