package com.fast.dev.ucenter.core.interceptors;

import com.fast.dev.core.interceptors.UrlInterceptor;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LogUrlInterceptor implements UrlInterceptor {

    //记录起始时间
    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Autowired
    private DBHelper dbHelper;

    @Override
    public String[] addPathPatterns() {
        return new String[]{"/**/*"};
    }

    @Override
    public String[] excludePathPatterns() {
        return new String[0];
    }

    @Override
    public int order() {
        return 0;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        startTime.set(this.dbHelper.getTime());
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            log.info(" [{}] - [{}] - {} - [ time : {} ] ", getClientIp(request), request.getRequestURI(), JsonUtil.toJson(request.getParameterMap()),this.dbHelper.getTime()-startTime.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        startTime.remove();
    }

    /**
     * 获取客户端ip
     * @param request
     * @return
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
