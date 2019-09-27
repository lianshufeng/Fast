package com.fast.dev.ucenter.core.interceptors;

import com.fast.dev.core.interceptors.UrlInterceptor;
import com.fast.dev.core.util.net.IPUtil;
import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.ucenter.core.service.RequestUrlLogService;
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

    @Autowired
    private RequestUrlLogService requestUrlLogService;

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
        this.requestUrlLogService.add(getClientIp(request), request.getRequestURI(), request.getQueryString(), request.getParameterMap(), this.dbHelper.getTime() - startTime.get());
//        try {
//            log.info(" [{}] - [{}] - {} - [ time : {} ] ", getClientIp(request), request.getRequestURI(), JsonUtil.toJson(request.getParameterMap()), this.dbHelper.getTime() - startTime.get());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        startTime.remove();
    }

    /**
     * 获取客户端ip
     *
     * @param request
     * @return
     */
    private String getClientIp(HttpServletRequest request) {
        return IPUtil.getRemoteIp(request);
    }

}
