package com.fast.dev.robot.service.inter;

import com.fast.dev.core.interceptors.UrlInterceptor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RobotTokenInterceptor implements UrlInterceptor {


    @Getter
    private ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<>();


    @Override
    public String[] addPathPatterns() {
        return new String[]{"/**/*"};
    }

    @Override
    public String[] excludePathPatterns() {
        return new String[]{"/resources/**"};
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.currentRequest.set(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        this.currentRequest.remove();
    }
}
