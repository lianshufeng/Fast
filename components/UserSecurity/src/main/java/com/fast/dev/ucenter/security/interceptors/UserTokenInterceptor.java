package com.fast.dev.ucenter.security.interceptors;

import com.fast.dev.core.interceptors.UrlInterceptor;
import com.fast.dev.core.mvc.MVCConfiguration;
import com.fast.dev.ucenter.security.helper.SecurityAuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 用户令牌拦截器
 */
public class UserTokenInterceptor implements UrlInterceptor {

    @Autowired
    private SecurityAuthenticationHelper securityAuthenticationHelper;

    @Override
    public String[] addPathPatterns() {
        return new String[]{"/**/*"};
    }

    @Override
    public String[] excludePathPatterns() {
        return new String[]{"/" + MVCConfiguration.StaticResources + "/**"};
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.securityAuthenticationHelper.authenticate(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        this.securityAuthenticationHelper.release();
    }
}
