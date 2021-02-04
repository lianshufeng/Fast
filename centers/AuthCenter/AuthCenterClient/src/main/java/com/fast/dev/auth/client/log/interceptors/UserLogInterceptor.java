package com.fast.dev.auth.client.log.interceptors;


import com.fast.dev.auth.client.log.helper.impl.UserLogHelperImpl;
import com.fast.dev.core.interceptors.UrlInterceptor;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserLogInterceptor implements UrlInterceptor {

    @Autowired
    private UserLogHelperImpl userLogHelper;


    @Override
    public String[] addPathPatterns() {
        return new String[]{"/**"};
    }

    @Override
    public String[] excludePathPatterns() {
        return new String[]{"/" + MVCConfiguration.StaticResources + "/**"};
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.userLogHelper.preEvent();
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        this.userLogHelper.afterEvent();
    }
}
